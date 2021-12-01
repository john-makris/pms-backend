package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Presence;
import gr.hua.pms.model.User;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId"
			+ " and (:filter is null or p.student.username like %:filter%"
			+ " or p.student.firstname like %:filter%"
			+ " or p.student.lastname like %:filter%)")
	Page<Presence> searchByClassSessionIdSortedPaginated(
			Long classSessionId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT p FROM Presence as p WHERE p.classSession.id=:classSessionId and p.student.id=:studentId")
	Presence searchByClassSessionIdAndStudentId(Long classSessionId, Long studentId);
	
	@Query(value = "Select user FROM ClassSession as cs JOIN cs.students user WHERE user.id=:studentId")
	User searchStudentByStudentId(Long studentId);
	
	public Presence findByStatus(Boolean status);
	
	Page<Presence> findAll(Pageable pageable);
	
	Page<Presence> findByStatusContaining(Boolean status, Pageable pageable);
	
	List<Presence> findByStatusContaining(Boolean status, Sort sort);

}