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
	
	@Query(value = "SELECT absence FROM Presence as absence WHERE absence.student.id=:studentId"
			+ " and (absence.excuseStatus=:excuseStatus or (absence.excuseStatus is null or :excuseStatus is null))"
			+ " and absence.classSession.lecture.courseSchedule.id=:courseScheduleId"
			+ " and absence.classSession.lecture.lectureType.name=:name")
	List<Presence> searchAbsencesByExcuseStatusAndCourseSchedule(Long studentId, Boolean excuseStatus, Long courseScheduleId, ELectureType name);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByClassSessionIdSortedPaginated(
			Long classSessionId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.student.id=:userId"
			+ " and (p.status=:status or (p.status is null and :status is null))"
			+ " and (p.excuseStatus=:excuseStatus or (p.excuseStatus is null and :excuseStatus is null))"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByUserIdStatusAndExcuseStatusSortedPaginated(
			Long userId, Boolean status, Boolean excuseStatus ,@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId and p.student.id=:studentId")
	Presence searchByClassSessionIdAndStudentId(Long classSessionId, Long studentId);
	
	@Query(value = "Select user FROM ClassSession as cs JOIN cs.students user WHERE user.id=:studentId")
	User searchStudentByStudentId(Long studentId);
	
	public Presence findByStatus(Boolean status);
	
	Page<Presence> findAll(Pageable pageable);
	
	Page<Presence> findByStatusContaining(Boolean status, Pageable pageable);
	
	List<Presence> findByStatusContaining(Boolean status, Sort sort);

}