package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.ExcuseApplication;
import gr.hua.pms.payload.request.ExcuseApplicationRequest;
import gr.hua.pms.payload.response.ExcuseApplicationResponse;

public interface ExcuseApplicationService {
	
	void deleteById(Long id);
	
	ExcuseApplicationResponse findExcuseApplicationResponseById(Long userId, Long id);

	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long userId, Long departmentId,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdAndCourseScheduleIdSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdAndTypeSortedPaginated(Long userId, Long departmentId,
			ELectureType name, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdAndStatusSortedPaginated(Long userId, Long departmentId,
			String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdTypeAndStatusSortedPaginated(Long userId, Long departmentId,
			ELectureType name, String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, ELectureType name,  String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, String status, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByCompleteSearchSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, ELectureType name, String status, String filter, int page, int size, String[] sort);
	
	/*Excuse Applications per User*/
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Map<String, Object> findAllByUserIdAndStatusSortedPaginated(Long userId, String status, String filter,
			int page, int size, String[] sort);
	
	public 	Map<String, Object> findAllByUserIdSortedPaginated(Long userId, String filter, int page, int size, String[] sort);

	public 	Map<String, Object> findAllByUserIdAndCourseScheduleIdSortedPaginated(Long userId, Long courseScheduleId,
			String filter, int page, int size, String[] sort);
	
	public 	Map<String, Object> findAllByUserIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType name, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByUserIdAndTypeSortedPaginated(Long userId, ELectureType name, String filter, int page,
			int size, String[] sort);
	
	public Map<String, Object> findAllByUserIdTypeAndStatusSortedPaginated(Long userId, ELectureType name, String typeOfStatus,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByUserIdCourseScheduleIdAndStatusSortedPaginated(Long userId, Long courseScheduleId,
			String typeOfStatus, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByUserCompleteSearchSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType name, String typeOfStatus, String filter, int page, int size, String[] sort);
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	List<ExcuseApplicationResponse> createExcuseApplicationsResponse(
			List<ExcuseApplication> excuseApplications, Long currentUserId);

	ExcuseApplicationResponse createExcuseApplicationResponse(ExcuseApplication excuseApplication, Long currentUserId);

	ExcuseApplication save(ExcuseApplicationRequest excuseApplicationRequestData, Long userId);
	
	ExcuseApplication update(Long id, ExcuseApplicationRequest excuseApplicationRequestData);
	
}