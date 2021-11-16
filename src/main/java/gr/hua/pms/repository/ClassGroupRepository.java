package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.ELectureType;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {

	Page<ClassGroup> findAll(Pageable pageable);
	
	Boolean existsByNameIdentifier(String nameIdentifier);
	
	Boolean existsByCourseSchedule(CourseSchedule courseSchedule);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.nameIdentifier=:nameIdentifier")
	List<ClassGroup> searchByCourseScheduleIdAndNameIdentifier(Long courseScheduleId, String nameIdentifier);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.groupType.name=:name and cg.nameIdentifier=:nameIdentifier")
	List<ClassGroup> searchByCourseScheduleIdAndLectureTypeNameAndNameIdentifier(Long courseScheduleId, ELectureType name, String nameIdentifier);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:courseScheduleId and cg.groupType.name=:name")
	List<ClassGroup> searchByCourseScheduleIdAndLectureTypeName(Long courseScheduleId, ELectureType name);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.id=:id and (:filter is null or cg.nameIdentifier like %:filter%)")
	Page<ClassGroup> searchByCourseScheduleAndFilterSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE (:filter is null or cg.room.roomIdentifier like %:filter% or cg.nameIdentifier like %:filter% or cg.courseSchedule.course.name like %:filter%)")
	Page<ClassGroup> searchByFilterSortedPaginated(@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.course.department.id=:departmentId and (:filter is null)")
	Page<ClassGroup> searchByDepartmentAndFilterSortedPaginated(Long departmentId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.course.department.id=:departmentId and cg.courseSchedule.id=:courseScheduleId and "
			+ "(:filter is null)")
	Page<ClassGroup> searchByCourseSchedulePerDepartmentAndFilterSortedPaginated(Long departmentId, Long courseScheduleId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cg FROM ClassGroup as cg WHERE cg.courseSchedule.course.department.id=:departmentId "
			+ "and cg.courseSchedule.id=:courseScheduleId and cg.groupType.name=:name "
			+ "and (:filter is null)")
	Page<ClassGroup> searchByCourseSchedulePerTypeAndDepartmentWithFilterSortedPaginated(Long departmentId, Long courseScheduleId, ELectureType name, @Param("filter") String filter, Pageable pageable);
}