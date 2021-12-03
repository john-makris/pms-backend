package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.custom.repository.ClassSessionRepositoryCustom;
import gr.hua.pms.model.ClassSession;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long>, ClassSessionRepositoryCustom {

	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and (:filter is null or cs.nameIdentifier like %:filter% or cs.startDateTime like %:filter% or cs.endDateTime like %:filter%"
			+ " or cs.classGroup.nameIdentifier like %:filter%)")
	Page<ClassSession> searchByLectureIdSortedPaginated(
			Long lectureId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and cs.nameIdentifier=:nameIdentifier")
	List<ClassSession> searchByLectureIdAndNameIdentifier(Long lectureId, String nameIdentifier);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and cs.classGroup.id=:classGroupId")
	List<ClassSession> searchByLectureIdClassGroupId(Long lectureId, Long classGroupId);
	
	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.students user WHERE cs.lecture.id=:lectureId"
			+ " and user.id=:studentId")
	ClassSession searchByLectureIdAndStudentId(Long lectureId, Long studentId);

	@Query(value = "SELECT cs FROM ClassSession as cs JOIN cs.students user WHERE cs.status=:status"
			+ " and user.id=:userId"
			+ " and (:filter is null or cs.lecture.courseSchedule.course.name like %:filter%"
			+ " or cs.lecture.nameIdentifier like %:filter% or cs.startDateTime like %:filter%"
			+ " or cs.endDateTime like %:filter%)")
	Page<ClassSession> searchByUserIdAndStatusSortedPaginated(Long userId, Boolean status, String filter,
			Pageable pagingSort);

	@Query(value = "SELECT p.classSession FROM Presence as p WHERE p.student.id=:studentId"
			+ " and p.status=true"
			+ " and p.classSession.status=:status")
	ClassSession searchPresentedClassSessionByStudentIdAndStatus(long studentId, Boolean status);
	
}