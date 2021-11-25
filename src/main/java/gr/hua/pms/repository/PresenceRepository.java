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
	
	public Presence findByStatus(Boolean status);
	
	Page<Presence> findAll(Pageable pageable);
	
	Page<Presence> findByStatusContaining(Boolean status, Pageable pageable);
	
	List<Presence> findByStatusContaining(Boolean status, Sort sort);

}