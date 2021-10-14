package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
	
	public Course findByName(String name);
	
	public boolean existsByName(String name);
	
	Page<Course> findAll(Pageable pageable);
	
	Page<Course> findAllByDepartmentId(Long id, Pageable pageable);
		
	Page<Course> findByNameContaining(String name, Pageable pageable);
		
	List<Course> findByNameContaining(String name, Sort sort);
	
	@Query(value = "SELECT c FROM Course c WHERE (:filter is null or c.name like %:filter% or c.semester.semesterName like %:filter%)")
	Page<Course> searchCoursesByFilterSortedPaginated(String filter, Pageable pageable);
	
	@Query(value = "SELECT c FROM Course c WHERE c.department.id=:departmentId and (:filter is null or c.name like %:filter% or c.semester.semesterName like %:filter%)")
	Page<Course> searchCoursesPerDepartmentByFilterSortedPaginated(Long departmentId, String filter, Pageable pageable);
}