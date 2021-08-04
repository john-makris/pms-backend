package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long>, JpaSpecificationExecutor<School> {

	public School findByName(String name);
	
	public boolean existsByName(String name);
	
	Page<School> findAll(Pageable pageable);
	
	Page<School> findByNameContaining(String name, Pageable pageable);
	
	List<School> findByNameContaining(String name, Sort sort);
	
}