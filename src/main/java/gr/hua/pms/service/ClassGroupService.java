package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.payload.request.ClassGroupRequest;
import gr.hua.pms.payload.response.ClassGroupResponse;

public interface ClassGroupService {
	
	public Map<String, Object> findAllByDepartmentAndCourseScheduleIdSortedPaginated(Long departmentId, Long courseScheduleId, String filter, int page, int size, String[] sort);

	public Map<String, Object> findAllByCourseScheduleIdPerTypeSortedPaginated(Long userId, Long courseScheduleId, 
			ELectureType name, String filter, int page, int size, String[] sort);

	public List<ClassGroup> findAll(String[] sort);
	
	public ClassGroupResponse findById(Long id, Long userId);
	
	public ClassGroup save(ClassGroupRequest classGroupRequestData, Long userId);
	
	public ClassGroup update(Long id, Long userId, ClassGroupRequest classGroupRequestData);
	
	public void deleteById(Long id, Long userId);
	
	public void deleteAll();

	List<ClassGroupResponse> createClassesGroupsResponse(List<ClassGroup> classesGroups);

	ClassGroupResponse createClassGroupResponse(ClassGroup classGroup);

	Map<String, Object> findAllByCourseScheduleIdPerTypeAndStatusSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType name, Boolean status, String filter, int page, int size, String[] sort);

	List<ClassGroupResponse> createCompletedClassesGroupsResponse(List<ClassGroup> classesGroups);

	boolean checkClassGroupCompleteness(ClassGroup classGroup);

	int classGroupCompletenessValidator(int capacity);

}