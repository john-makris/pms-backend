package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

	public Lecture findByPresenceStatementStatus(Boolean status);
		
	Page<Lecture> findAll(Pageable pageable);
	
	Page<Lecture> findByPresenceStatementStatusContaining(Boolean status, Pageable pageable);
	
	List<Lecture> findByPresenceStatementStatusContaining(Boolean status, Sort sort);
}