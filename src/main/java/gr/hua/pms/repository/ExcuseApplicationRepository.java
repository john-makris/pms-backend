package gr.hua.pms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.ExcuseApplication;

public interface ExcuseApplicationRepository extends JpaRepository<ExcuseApplication, Long> {
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl"
			+ " WHERE appl.id=:excuseApplicationId"
			+ " and appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}")
	ExcuseApplication checkSecretaryOwnershipByExcuseApplicationId(Long excuseApplicationId);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl"
			+ " WHERE appl.id=:excuseApplicationId"
			+ " and appl.absence.student.id = ?#{principal?.id}")
	ExcuseApplication checkStudentOwnershipByExcuseApplicationId(Long excuseApplicationId);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.id=:presenceId")
	ExcuseApplication searchByPresenceId(Long presenceId);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdSortedPaginated(
			Long departmentId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdSortedPaginated(Long departmentId, String filter,
			Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdAndCourseScheduleIdSortedPaginated(
			Long departmentId, Long courseScheduleId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdAndCourseScheduleIdSortedPaginated(Long departmentId,
			Long courseScheduleId, String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdAndTypeSortedPaginated(
			Long departmentId, ELectureType name, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdAndTypeSortedPaginated(Long departmentId, ELectureType name,
			String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdAndStatusSortedPaginated(
			Long departmentId, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdAndStatusSortedPaginated(
			Long departmentId, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdTypeAndStatusSortedPaginated(
			Long departmentId, ELectureType name, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdTypeAndStatusSortedPaginated(Long departmentId, ELectureType name,
			Boolean status, String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(
			Long departmentId, Long courseScheduleId, ELectureType name, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdCourseScheduleIdAndTypeSortedPaginated(Long departmentId,
			Long courseScheduleId, ELectureType name, String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(
			Long departmentId, Long courseScheduleId, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByOwnerDepartmentIdCourseScheduleIdAndStatusSortedPaginated(
			Long departmentId, Long courseScheduleId, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> completeSearch(Long departmentId, Long courseScheduleId, ELectureType name, Boolean status, 
			@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE"
			+ " appl.absence.classSession.lecture.courseSchedule.course.department.id = ?#{principal?.department?.id}"
			+ " and appl.absence.student.department.id=:departmentId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> completeOwnerSearch(Long departmentId, Long courseScheduleId, ELectureType name, Boolean status, 
			@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE (appl.status=:status or (appl.status is null and :status is null))"
			+ " and appl.absence.student.id=:userId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.classSession.endDateTime like %:filter%"
			+ " or appl.absence.classSession.startDateTime like %:filter% or appl.absence.classSession.classGroup.startTime like %:filter%"
			+ " or appl.absence.classSession.classGroup.endTime like %:filter%)")
	Page<ExcuseApplication> searchByUserIdAndStatusSortedPaginated(Long userId, Boolean status, String filter,
			Pageable pageable);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByUserIdSortedPaginated(
			Long userId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByUserIdAndCourseScheduleIdSortedPaginated(
			Long userId, Long courseScheduleId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByUserIdAndTypeSortedPaginated(
			Long userId, ELectureType name, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByUserIdTypeAndStatusSortedPaginated(
			Long userId, ELectureType name, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByUserIdCourseScheduleIdAndTypeSortedPaginated(
			Long userId, Long courseScheduleId, ELectureType name, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> searchByUserIdCourseScheduleIdAndStatusSortedPaginated(
			Long userId, Long courseScheduleId, Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.student.id=:userId"
			+ " and appl.absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and appl.absence.classSession.lecture.lectureType.name=:name"
			+ " and (appl.status=:status or (appl.status is null and :status is null))"
			+ " and (:filter is null or appl.absence.classSession.lecture.courseSchedule.course.name like %:filter%"
			+ " or appl.absence.classSession.lecture.nameIdentifier like %:filter% or appl.absence.student.username like %:filter%)")
	Page<ExcuseApplication> completeByUserSearch(Long userId, Long courseScheduleId, ELectureType name, Boolean status, 
			@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT appl FROM ExcuseApplication as appl WHERE appl.absence.id=:absenceId")
	ExcuseApplication searchByAbsenceId(Long absenceId);
	
}