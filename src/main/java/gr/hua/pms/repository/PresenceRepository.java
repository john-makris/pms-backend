package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Presence;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {

	public Presence findByLectureTitle(String title);
	
	public Presence findByPresenceStatus(Boolean status);
	
	Page<Presence> findAll(Pageable pageable);
	
	Page<Presence> findByPresenceStatusContaining(Boolean status, Pageable pageable);
	
	List<Presence> findByPresenceStatusContaining(Boolean status, Sort sort);
	
	Page<Presence> findByLectureTitleContaining(String title, Pageable pageable);
	
	List<Presence> findByLectureTitleContaining(String title, Sort sort);
}