package gr.hua.pms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>  {
	
	Optional<User> findByUsername(String username);
	
	Optional<User> findByAm(String am);
		
	Page<User> findAll(Pageable pageable);
	
	Boolean existsByAm(String am);
	
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
	
	Optional<User> findByDepartmentId(Long departmentId);
	
	@Query(value = "SELECT u FROM User u JOIN u.roles r WHERE u.department.id=:id and (:roleId is null or r.id=:roleId) and"
			+ " (:filter is null or u.username like %:filter% or u.firstname like %:filter% or u.lastname like %:filter%)")
	Page<User> searchPerDepartmentByRoleSortedPaginated(Long id, Integer roleId, String filter, Pageable pageable);
	
	@Query(value = "SELECT u FROM User u JOIN u.roles r WHERE r.id=:roleId and (:filter is null or u.username like %:filter%)")
	Page<User> searchByRoleSortedPaginated(Integer roleId, String filter, Pageable pageable);
	
	@Query(value = "SELECT user FROM CourseSchedule cs JOIN cs.students user WHERE cs.id=:id and "
			+ "(:filter is null or user.username like %:filter% or user.firstname like %:filter% or user.lastname like %:filter%)")
	Page<User> searchStudentsPerCourseScheduleByFilterSortedPaginated(Long id, String filter, Pageable pageable);

	@Query(value = "SELECT user FROM CourseSchedule cs JOIN cs.students user WHERE user.id=:id")
	Optional<User> findCourseScheduleStudentById(Long id);
	
	@Query(value = "SELECT u FROM User u JOIN u.roles r WHERE :roleId is null or r.id=:roleId")
	List<User> findByRole(Integer roleId);
	
	@Query(value = "SELECT user FROM CourseSchedule cs JOIN cs.students user WHERE cs.id=:courseScheduleId"
			+ " and user.id NOT IN (Select gs.student FROM GroupStudent as gs WHERE gs.classGroup.courseSchedule.id=:courseScheduleId"
			+ " and gs.classGroup.groupType.id=:classGroupTypeId)"
			+ " and (:filter is null or user.username like %:filter% or user.firstname like %:filter% or user.lastname like %:filter%)")
	Page<User> searchStudentsWithoutGroup(Long courseScheduleId, Integer classGroupTypeId, String filter, Pageable pageable);
	
	@Query(value = "SELECT user FROM CourseSchedule cs JOIN cs.students user JOIN cs.teachingStuff teacher WHERE cs.id=:courseScheduleId"
			+ " and user.id NOT IN (Select gs.student FROM GroupStudent as gs WHERE gs.classGroup.courseSchedule.id=:courseScheduleId"
			+ " and gs.classGroup.groupType.id=:classGroupTypeId)"
			+ " and teacher.id = ?#{principal?.id}"
			+ " and (:filter is null or user.username like %:filter% or user.firstname like %:filter% or user.lastname like %:filter%)")
	Page<User> searchOwnerStudentsWithoutGroup(Long courseScheduleId, Integer classGroupTypeId, String filter, Pageable pageable);
	
	@Query(value = "SELECT user FROM ClassSession as cs JOIN cs.students user WHERE cs.id=:classSessionId"
			+ " and (:filter is null or user.username like %:filter% or user.firstname like %:filter% or user.lastname like %:filter%)")
	Page<User> searchStudentsByClassSessionIdSortedPaginated(Long classSessionId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT user FROM ClassSession as cs JOIN cs.students user JOIN cs.lecture.courseSchedule.teachingStuff teacher"
			+ " WHERE cs.id=:classSessionId"
			+ " and teacher.id = ?#{principal?.id}"
			+ " and (:filter is null or user.username like %:filter% or user.firstname like %:filter% or user.lastname like %:filter%)")
	Page<User> searchStudentsByOwnerClassSessionIdSortedPaginated(Long classSessionId, String filter,
			Pageable pagingSort);
}