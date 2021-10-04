package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.ActiveCourse;

@Repository
public interface ActiveCourseRepository extends JpaRepository<ActiveCourse, Long>, JpaSpecificationExecutor<ActiveCourse> {
			
	Page<ActiveCourse> findAll(Pageable pageable);
	
	@Query(value = "SELECT a FROM ActiveCourse as a WHERE a.course.department.id=:id and (:filter is null or a.course.name like %:filter% or a.course.semester like %:filter%)")
	Page<ActiveCourse> searchPerDepartmentByFilterSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT a FROM ActiveCourse as a WHERE :filter is null or a.course.name like %:filter% or a.course.semester like %:filter%")
	Page<ActiveCourse> searchByFilterSortedPaginated(@Param("filter") String filter, Pageable pageable);
	
	public ActiveCourse findByCourseName(String name);
	
	public ActiveCourse findByCourseId(Long id);
	
	public boolean existsByCourseName(String name);
	
	public boolean existsByCourseId(Long id);
		
	Page<ActiveCourse> findAllByCourseId(Long id, Pageable pageable);
		
	Page<ActiveCourse> findByCourseNameContaining(String name, Pageable pageable);
		
	List<ActiveCourse> findByCourseNameContaining(String name, Sort sort);
}