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

import gr.hua.pms.custom.repository.CourseScheduleRepositoryCustom;
import gr.hua.pms.model.CourseSchedule;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long>, JpaSpecificationExecutor<CourseSchedule>,
	CourseScheduleRepositoryCustom {
			
	Page<CourseSchedule> findAll(Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs JOIN cs.teachingStuff as user WHERE"
			+ " cs.id=:courseScheduleId"
			+ " and user.id = ?#{principal?.id}")
	CourseSchedule checkOwnershipByCourseScheduleId(Long courseScheduleId);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs WHERE cs.course.department.id=:id and (cs.status is null or cs.status=true) and (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchPerDepartmentByStatusAndFilterSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs WHERE cs.course.department.id=:id and"
			+ " (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchPerDepartmentSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs JOIN cs.teachingStuff user WHERE"
			+ " (cs.course.department.id=:id and user.id = ?#{principal?.id})"
			+ " and (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchByTeacherOwnerPerDepartmentSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs JOIN cs.students user WHERE"
			+ " (cs.course.department.id=:id and user.id = ?#{principal?.id})"
			+ " and (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchByStudentOwnerPerDepartmentSortedPaginated(Long id, @Param("filter") String filter, Pageable pagingSort);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs WHERE"
			+ " (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchAllFilterSortedPaginated(@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM CourseSchedule as cs WHERE cs.course.department.id=:id"
			+ " and (cs.status=:status or (cs.status is null and :status is null))"
			+ " and (:filter is null or cs.course.name like %:filter% or cs.course.semester like %:filter%)")
	Page<CourseSchedule> searchPerDepartmentByAllStatusAndFilterSortedPaginated(Long id, Boolean status, String filter, Pageable pagingSort);
	
	public CourseSchedule findByCourseName(String name);
	
	public CourseSchedule findByCourseId(Long id);
	
	public boolean existsByCourseName(String name);
	
	public boolean existsByCourseId(Long id);
	
	public boolean existsByCourseIdAndAcademicYear(Long id, String academicYear);
		
	Page<CourseSchedule> findAllByCourseId(Long id, Pageable pageable);
		
	Page<CourseSchedule> findByCourseNameContaining(String name, Pageable pageable);
		
	List<CourseSchedule> findByCourseNameContaining(String name, Sort sort);
	
}