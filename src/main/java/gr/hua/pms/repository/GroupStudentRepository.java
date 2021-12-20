package gr.hua.pms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.GroupStudent;
import gr.hua.pms.model.User;

public interface GroupStudentRepository extends JpaRepository<GroupStudent, Long> {

	Page<GroupStudent> findAll(Pageable pageable);
	
	Boolean existsByClassGroupId(Long classGroupId);
	
	Boolean existsByStudentIdAndClassGroupId(Long studentId, Long classGroupId);
	
	@Query(value = "SELECT gs FROM GroupStudent as gs JOIN gs.classGroup.courseSchedule.teachingStuff as user WHERE"
			+ " gs.id=:groupStudentId"
			+ " and user.id = ?#{principal?.id}")
	GroupStudent checkTeacherOwnerShipByGroupStudentId(Long groupStudentId);
	
	@Query(value = "SELECT gs FROM GroupStudent as gs JOIN gs.classGroup.courseSchedule.students as user WHERE"
			+ " gs.id=:groupStudentId"
			+ " and user.id = ?#{principal?.id}")
	GroupStudent checkStudentOwnerShipByGroupStudentId(Long groupStudentId);
	
	@Query(value = "SELECT gs FROM GroupStudent as gs WHERE gs.classGroup.id=:classGroupId")
	List<GroupStudent> searchByClassGroupId(Long classGroupId);
	
	@Query(value = "SELECT gs FROM GroupStudent as gs WHERE gs.classGroup.id=:classGroupId and gs.student.id=:studentId")
	GroupStudent searchByClassGroupIdAndStudentId(Long classGroupId, Long studentId);
	
	@Query(value = "SELECT gs.classGroup FROM GroupStudent as gs WHERE gs.student.id=:studentId and gs.classGroup.groupType.name=:groupType")
	ClassGroup searchByStudentIdAndGroupType(Long studentId, ELectureType groupType);
	
	@Query(value = "SELECT gs.classGroup FROM GroupStudent as gs"
			+ " WHERE gs.classGroup.groupType.name=:groupType"
			+ " and gs.classGroup.courseSchedule.id=:courseScheduleId"
			+ " and gs.student.id=:studentId"
			+ " and gs.student.id = ?#{principal?.id}")
	ClassGroup searchClassGroupByStudentIdAndCourseScheduleIdAndGroupType(ELectureType groupType, Long courseScheduleId, Long studentId);
	
	@Query(value = "SELECT gs FROM GroupStudent as gs WHERE gs.classGroup.groupType.name=:groupType and gs.classGroup.courseSchedule.id=:courseScheduleId and gs.student.id=:studentId")
	List<GroupStudent> searchByStudentIdAndCourseScheduleIdAndGroupType(ELectureType groupType, Long courseScheduleId, Long studentId);
	
	@Query(value = "SELECT gs FROM GroupStudent as gs WHERE gs.classGroup.courseSchedule.course.department.id=:departmentId "
			+ "and gs.classGroup.courseSchedule.id=:courseScheduleId and gs.classGroup.groupType.name=:name "
			+ "and gs.classGroup.id=:classGroupId "
			+ "and (:filter is null)")
	Page<GroupStudent> searchByDepartmentCourseSchedulePerTypeAndClassGroupWithFilterSortedPaginated(
			Long departmentId, Long courseScheduleId, Long classGroupId, ELectureType name, 
			@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT gs.student FROM GroupStudent as gs WHERE gs.classGroup.id=:classGroupId"
			+ " and (:filter is null)")
	Page<User> searchStudentsOfGroupWithFilterSortedPaginated(Long classGroupId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT gs.student FROM GroupStudent as gs JOIN gs.classGroup.courseSchedule.teachingStuff user"
			+ " WHERE gs.classGroup.id=:classGroupId"
			+ " and user.id = ?#{principal?.id}"
			+ " and (:filter is null)")
	Page<User> searchOwnerStudentsOfGroupWithFilterSortedPaginated(Long classGroupId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT gs.student FROM GroupStudent as gs WHERE gs.classGroup.id=:classGroupId")
	Set<User> searchStudentsOfGroup(Long classGroupId);
	
	@Query(value = "SELECT gs.student FROM GroupStudent as gs WHERE gs.student.id=:studentId and gs.classGroup.id=:classGroupId")
	User searchStudentOfGroup(Long studentId, Long classGroupId);
}
