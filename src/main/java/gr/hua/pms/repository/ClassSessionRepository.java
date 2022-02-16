package gr.hua.pms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.custom.repository.ClassSessionRepositoryCustom;
import gr.hua.pms.model.ClassSession;
import gr.hua.pms.model.User;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long>, ClassSessionRepositoryCustom {
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE cs.id=:classSessionId"
			+ " and user.id = ?#{principal?.id}")
	ClassSession checkTeacherOwnershipByClassSessionId(Long classSessionId);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.students as user"
			+ " WHERE cs.id=:classSessionId"
			+ " and user.id = ?#{principal?.id}")
	ClassSession checkStudentOwnershipByClassSessionId(Long classSessionId);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and (:filter is null or cs.nameIdentifier like %:filter% or cs.startDateTime like %:filter% or cs.endDateTime like %:filter%"
			+ " or cs.classGroup.nameIdentifier like %:filter%)")
	Page<ClassSession> searchByLectureIdSortedPaginated(
			Long lectureId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE cs.lecture.id=:lectureId"
			+ " and user.id = ?#{principal?.id}"
			+ " and (:filter is null or cs.nameIdentifier like %:filter% or cs.startDateTime like %:filter% or cs.endDateTime like %:filter%"
			+ " or cs.classGroup.nameIdentifier like %:filter%)")
	Page<ClassSession> searchByOwnerLectureIdSortedPaginated(Long lectureId, String filter, Pageable pagingSort);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.classGroup.id=:classGroupId")
	List<ClassSession> searchByClassGroupId(Long classGroupId);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId")
	List<ClassSession> searchByLectureId(Long lectureId);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ " and (cs.status=:status or (cs.status is null and :status is null))"
			+ "and (:filter is null or cs.nameIdentifier like %:filter% or cs.startDateTime like %:filter% or cs.endDateTime like %:filter%"
			+ " or cs.classGroup.nameIdentifier like %:filter%)")
	Page<ClassSession> searchByLectureIdAndStatusSortedPaginated(Long lectureId, Boolean status,
			String filter, Pageable pagingSort);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.lecture.courseSchedule.teachingStuff as user"
			+ " WHERE cs.lecture.id=:lectureId "
			+ " and user.id = ?#{principal?.id}"
			+ " and (cs.status=:status or (cs.status is null and :status is null))"
			+ "and (:filter is null or cs.nameIdentifier like %:filter% or cs.startDateTime like %:filter% or cs.endDateTime like %:filter%"
			+ " or cs.classGroup.nameIdentifier like %:filter%)")
	Page<ClassSession> searchByOwnerLectureIdAndStatusSortedPaginated(Long lectureId, Boolean status,
			String filter, Pageable pagingSort);	
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.students user WHERE cs.status=:status"
			+ " and user.id=:userId"
			+ " and user.id = ?#{principal?.id}"
			+ " and (:filter is null or cs.lecture.courseSchedule.course.name like %:filter%"
			+ " or cs.lecture.nameIdentifier like %:filter% or cs.startDateTime like %:filter%"
			+ " or cs.endDateTime like %:filter%)")
	Page<ClassSession> searchByUserIdAndStatusSortedPaginated(Long userId, Boolean status, String filter,
			Pageable pagingSort);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and cs.nameIdentifier=:nameIdentifier")
	List<ClassSession> searchByLectureIdAndNameIdentifier(Long lectureId, String nameIdentifier);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and cs.classGroup.id=:classGroupId")
	List<ClassSession> searchByLectureIdClassGroupId(Long lectureId, Long classGroupId);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.lecture.courseSchedule.teachingStuff teacher WHERE"
			+ " ((cs.startDateTime <= :startDateTime and :startDateTime < cs.endDateTime)"
			+ " or (cs.startDateTime < :endDateTime and :endDateTime <= cs.endDateTime))"
			+ " and cs.classGroup.room.roomIdentifier=:roomIdentifier")
	ClassSession checkClassSessionBasicValidity(LocalDateTime startDateTime, LocalDateTime endDateTime, String roomIdentifier);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.lecture.courseSchedule.teachingStuff teacher WHERE"
			+ " ((cs.startDateTime <= :startDateTime and :startDateTime < cs.endDateTime)"
			+ " or (cs.startDateTime < :endDateTime and :endDateTime <= cs.endDateTime))"
			+ " and teacher IN :teachers")
	ClassSession checkClassSessionTeacherValidity(LocalDateTime startDateTime, LocalDateTime endDateTime, List<User> teachers);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.students user WHERE cs.lecture.id=:lectureId"
			+ " and user.id=:studentId")
	ClassSession searchByLectureIdAndStudentId(Long lectureId, Long studentId);

	@Query(value = "SELECT cs FROM Presence as p JOIN p.classSession as cs WHERE"
			+ " p.student.id=:studentId"
			+ " and p.student.id = ?#{principal?.id}"
			+ " and p.status=:presenceStatus"
			+ " and cs.status=:classSessionStatus")
	ClassSession searchCurrentClassSessionByStudentIdAndPresenceStatus(Long studentId, Boolean presenceStatus, Boolean classSessionStatus);
	
	@Query(value = "SELECT cs FROM Presence as p JOIN p.classSession as cs WHERE"
			+ " p.student.id=:studentId"
			+ " and p.status=:presenceStatus"
			+ " and cs.status=:classSessionStatus")
	ClassSession checkStudentPresenceValidity(Long studentId, Boolean presenceStatus, Boolean classSessionStatus);
}