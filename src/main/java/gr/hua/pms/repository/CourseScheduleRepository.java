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

import gr.hua.pms.model.CourseSchedule;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long>, JpaSpecificationExecutor<CourseSchedule> {
			
	Page<CourseSchedule> findAll(Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs WHERE cs.course.department.id=:id and (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchPerDepartmentByFilterSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs WHERE :filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%")
	Page<CourseSchedule> searchByFilterSortedPaginated(@Param("filter") String filter, Pageable pageable);
	
	public CourseSchedule findByCourseName(String name);
	
	public CourseSchedule findByCourseId(Long id);
	
	public boolean existsByCourseName(String name);
	
	public boolean existsByCourseId(Long id);
		
	Page<CourseSchedule> findAllByCourseId(Long id, Pageable pageable);
		
	Page<CourseSchedule> findByCourseNameContaining(String name, Pageable pageable);
		
	List<CourseSchedule> findByCourseNameContaining(String name, Sort sort);
}