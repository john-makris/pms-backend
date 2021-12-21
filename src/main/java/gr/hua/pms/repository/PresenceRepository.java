package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.Presence;
import gr.hua.pms.model.User;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {
	
	@Query(value = "SELECT p FROM Presence as p JOIN p.classSession.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE p.id=:presenceId"
			+ " and user.id = ?#{principal?.id}")
	Presence checkOwnershipByPresenceId(Long presenceId);
	
	@Query(value = "SELECT absence FROM Presence as absence WHERE absence.student.id=:studentId"
			+ " and (absence.excuseStatus=:excuseStatus or (absence.excuseStatus is null and :excuseStatus is null))"
			+ " and absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and absence.classSession.lecture.lectureType.name=:name")
	List<Presence> searchAbsencesByExcuseStatusAndCourseSchedule(Long studentId, Boolean excuseStatus, Long courseScheduleId, ELectureType name);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByClassSessionIdSortedPaginated(
			Long classSessionId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p JOIN p.classSession.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE p.classSession.id=:classSessionId"
			+ " and user.id = ?#{principal?.id}"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByOwnerClassSessionIdSortedPaginated(Long classSessionId, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByClassSessionIdAndStatusSortedPaginated(Long classSessionId, Boolean status, String filter,
			Pageable pagingSort);
	
	@Query(value = "SELECT p FROM Presence as p JOIN p.classSession.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE p.classSession.id=:classSessionId"
			+ " and user.id = ?#{principal?.id}"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByOwnerClassSessionIdAndStatusSortedPaginated(Long classSessionId,
			Boolean status, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (p.excuseStatus=:excuseStatus or (p.excuseStatus is null and :excuseStatus is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByClassSessionIdStatusAndExcuseStatusSortedPaginated(Long classSessionId, Boolean status,
			Boolean excuseStatus, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT p FROM Presence as p JOIN p.classSession.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE p.classSession.id=:classSessionId"
			+ " and user.id = ?#{principal?.id}"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (p.excuseStatus=:excuseStatus or (p.excuseStatus is null and :excuseStatus is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByOwnerClassSessionIdStatusAndExcuseStatusSortedPaginated(Long classSessionId,
			Boolean status, Boolean excuseStatus, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.student.id=:userId"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (p.excuseStatus=:excuseStatus or (p.excuseStatus is null and :excuseStatus is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByUserIdStatusAndExcuseStatusSortedPaginated(
			Long userId, Boolean status, Boolean excuseStatus ,@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.student.id=:userId"
			+ " and p.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and p.classSession.lecture.lectureType.name=:lectureType"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%"
			+ " or p.classSession.lecture.nameIdentifier like %:filter%"
			+ " or p.classSession.startDateTime like %:filter%"
			+ " or p.classSession.endDateTime like %:filter%)")
	Page<Presence> searchByUserIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.student.id=:userId"
			+ " and p.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and p.classSession.lecture.lectureType.name=:lectureType"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%"
			+ " or p.classSession.lecture.nameIdentifier like %:filter%"
			+ " or p.classSession.startDateTime like %:filter%"
			+ " or p.classSession.endDateTime like %:filter%)")
	Page<Presence> searchByUserIdCourseScheduleIdTypeAndStatusSortedPaginated(Long userId, Long courseScheduleId, ELectureType lectureType, 
			Boolean status, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.student.id=:userId"
			+ " and p.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and p.classSession.lecture.lectureType.name=:lectureType"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (p.excuseStatus=:excuseStatus or (p.excuseStatus is null and :excuseStatus is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%"
			+ " or p.classSession.lecture.nameIdentifier like %:filter%"
			+ " or p.classSession.startDateTime like %:filter%"
			+ " or p.classSession.endDateTime like %:filter%)")
	Page<Presence> searchByAllParametersSortedPaginated(Long userId, Long courseScheduleId, ELectureType lectureType, 
			Boolean status, Boolean excuseStatus ,@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId and p.student.id=:studentId")
	Presence searchByClassSessionIdAndStudentId(Long classSessionId, Long studentId);
	
	@Query(value = "Select user FROM ClassSession as cs JOIN cs.students user WHERE user.id=:studentId")
	User searchStudentByStudentId(Long studentId);
	
	public Presence findByStatus(Boolean status);
	
	Page<Presence> findAll(Pageable pageable);
	
	Page<Presence> findByStatusContaining(Boolean status, Pageable pageable);
	
	List<Presence> findByStatusContaining(Boolean status, Sort sort);

	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId")
	List<Presence> searchByClassSessionId(Long classSessionId);

}