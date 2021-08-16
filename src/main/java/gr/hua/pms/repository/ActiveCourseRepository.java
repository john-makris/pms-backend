package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.ActiveCourse;

@Repository
public interface ActiveCourseRepository extends JpaRepository<ActiveCourse, Long>, JpaSpecificationExecutor<ActiveCourse> {
			
	Page<ActiveCourse> findAll(Pageable pageable);
	
	public ActiveCourse findByCourseName(String name);
	
	public ActiveCourse findByCourseId(Long id);
	
	public boolean existsByCourseName(String name);
		
	Page<ActiveCourse> findAllByCourseId(Long id, Pageable pageable);
		
	Page<ActiveCourse> findByCourseNameContaining(String name, Pageable pageable);
		
	List<ActiveCourse> findByCourseNameContaining(String name, Sort sort);
}