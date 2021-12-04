package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.ExcuseApplication;
import gr.hua.pms.payload.response.ExcuseApplicationResponse;

public interface ExcuseApplicationService {

	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long departmentId,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdAndCourseScheduleIdSortedPaginated(Long departmentId,
			Long courseScheduleId, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdAndTypeSortedPaginated(Long departmentId,
			ELectureType name, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdAndStatusSortedPaginated(Long departmentId,
			String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdTypeAndStatusSortedPaginated(Long departmentId,
			ELectureType name, String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(Long departmentId,
			Long courseScheduleId, ELectureType name,  String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(Long departmentId,
			Long courseScheduleId, String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByCompleteSearchSortedPaginated(Long departmentId,
			Long courseScheduleId, ELectureType name, String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByUserIdAndStatusSortedPaginated(Long userId, String status, String filter,
			int page, int size, String[] sort);

	List<ExcuseApplicationResponse> createExcuseApplicationsResponse(
			List<ExcuseApplication> excuseApplications);

	ExcuseApplicationResponse createExcuseApplicationResponse(ExcuseApplication excuseApplication);
}