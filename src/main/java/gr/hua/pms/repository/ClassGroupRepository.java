package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.ELectureType;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {

	Page<ClassGroup> findAll(Pageable pageable);
	
	Boolean existsByNameIdentifier(String nameIdentifier);
	
	Boolean existsByCourseSchedule(CourseSchedule courseSchedule);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg JOIN cg.courseSchedule.teachingStuff as user WHERE"
			+ " cg.id=:classGroupId"
			+ " and user.id = ?#{principal?.id}")
	ClassGroup checkOwnerShipByClassGroupId(Long classGroupId);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg JOIN cg.courseSchedule.students as user WHERE"
			+ " cg.id=:classGroupId"
			+ " and user.id = ?#{principal?.id}")
	ClassGroup checkStudentOwnershipByClassGroupId(Long classGroupId);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg JOIN cg.courseSchedule.students as user WHERE"
			+ " cg.id=:classGroupId"
			+ " and user.id =:studentId")
	ClassGroup checkStudentOwnershipByClassGroupIdAndStudentId(Long classGroupId, Long studentId);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId")
	List<ClassGroup> searchByCourseScheduleId(Long courseScheduleId);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.nameIdentifier=:nameIdentifier")
	List<ClassGroup> searchByCourseScheduleIdAndNameIdentifier(Long courseScheduleId, String nameIdentifier);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.groupType.name=:name and cg.nameIdentifier=:nameIdentifier")
	List<ClassGroup> searchByCourseScheduleIdAndLectureTypeNameAndNameIdentifier(Long courseScheduleId, ELectureType name, String nameIdentifier);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.groupType.name=:name")
	List<ClassGroup> searchByCourseScheduleIdAndLectureTypeName(Long courseScheduleId, ELectureType name);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:id and (:filter is null or cg.nameIdentifier like %:filter%)")
	Page<ClassGroup> searchByCourseScheduleAndFilterSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE (:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% or cg.courseSchedule.course.name like %:filter%)")
	Page<ClassGroup> searchByFilterSortedPaginated(@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.course.department.id=:departmentId and (:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% or cg.courseSchedule.course.name like %:filter%)")
	Page<ClassGroup> searchByDepartmentAndFilterSortedPaginated(Long departmentId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.course.department.id=:departmentId and cg.courseSchedule.id=:courseScheduleId and "
			+ "(:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% or cg.courseSchedule.course.name like %:filter%)")
	Page<ClassGroup> searchByCourseSchedulePerDepartmentAndFilterSortedPaginated(Long departmentId, Long courseScheduleId, @Param("filter") String filter, Pageable pageable);

	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.groupType.name=:name and "
			+ "(:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% "
			+ "or cg.courseSchedule.course.name like %:filter% or cg.startTime like %:filter% or cg.endTime like %:filter%)")
	Page<ClassGroup> searchByCourseSchedulePerTypeWithFilterSortedPaginated(Long courseScheduleId, ELectureType name, @Param("filter") String filter, Pageable pageable);

	@Query(value = "SELECT cg FROM ClassGroup as cg JOIN cg.courseSchedule.teachingStuff user"
			+ " WHERE cg.courseSchedule.id=:courseScheduleId"
			+ " and user.id = ?#{principal?.id}"
			+ " and cg.groupType.name=:name and "
			+ "(:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% "
			+ "or cg.courseSchedule.course.name like %:filter% or cg.startTime like %:filter% or cg.endTime like %:filter%)")
	Page<ClassGroup> searchByTeacherOwnerCourseSchedulePerTypeWithFilterSortedPaginated(Long courseScheduleId, 
			ELectureType name, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg JOIN cg.courseSchedule.students user"
			+ " WHERE cg.courseSchedule.id=:courseScheduleId"
			+ " and user.id = ?#{principal?.id}"
			+ " and cg.groupType.name=:name and (:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% or cg.courseSchedule.course.name like %:filter%)")
	Page<ClassGroup> searchByStudentOwnerCourseSchedulePerTypeWithFilterSortedPaginated(Long courseScheduleId,
			ELectureType name, @Param("filter") String filter, Pageable pagingSort);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId"
			+ " and cg.groupType.name=:name"
			+ " and cg.status=:status"
			+ " and (:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter%"
			+ "	or cg.courseSchedule.course.name like %:filter% or cg.startTime like %:filter% or cg.endTime like %:filter%)")
	Page<ClassGroup> searchByCourseSchedulePerTypeAndStatusWithFilterSortedPaginated(Long courseScheduleId,
			ELectureType name, Boolean status, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg JOIN cg.courseSchedule.teachingStuff user"
			+ " WHERE cg.courseSchedule.id=:courseScheduleId"
			+ " and user.id = ?#{principal?.id}"
			+ " and cg.groupType.name=:name"
			+ " and cg.status=:status"
			+ " and (:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter%"
			+ "	or cg.courseSchedule.course.name like %:filter% or cg.startTime like %:filter% or cg.endTime like %:filter%)")
	Page<ClassGroup> searchByTeacherOwnerCourseSchedulePerTypeAndStatusWithFilterSortedPaginated(Long courseScheduleId,
			ELectureType name, Boolean status, String filter, Pageable pagingSort);
}