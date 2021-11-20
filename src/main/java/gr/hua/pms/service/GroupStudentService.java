package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.GroupStudent;
import gr.hua.pms.payload.request.GroupStudentRequestData;
import gr.hua.pms.payload.response.UserResponse;

public interface GroupStudentService {
	
	public Map<String, Object> findAllByDepartmentCourseSchedulePerTypeAndClassGroupSortedPaginated(
			Long departmentId, Long courseScheduleId, Long classGroupId,
			ELectureType name, String filter, int page, int size, String[] sort);

	public Map<String, Object> findStudentsOfGroup(Long classGroupId,
			String filter, int page, int size, String[] sort);
	
	public UserResponse findStudentOfGroup(Long studentId, Long classGroupId);
	
	public List<GroupStudent> findAll(String[] sort);
	
	public GroupStudent findById(Long id);
	
	public ClassGroup findClassGroupByStudentIdAndCourseScheduleIdAndGroupType(Long studentId, Long courseScheduleId, ELectureType groupType);
	
	GroupStudent save(GroupStudentRequestData groupStudentRequestData);
	
	public GroupStudent update(Long id, GroupStudent groupStudent);
	
	public void deleteById(Long id);
	
	public void deleteByClassGroupIdAndStudentId(Long classGroupId, Long studentId);
	
	public void deleteAll();
}
