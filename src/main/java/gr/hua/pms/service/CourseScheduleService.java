package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.payload.request.CourseScheduleRequest;
import gr.hua.pms.payload.response.CourseScheduleResponse;

public interface CourseScheduleService {

	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
			
	public Map<String, Object> findAllByCourseDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort);
		
	public List<CourseSchedule> findAll(String[] sort);
	
	public CourseScheduleResponse findById(Long id);
	
	public CourseSchedule findByCourseId(Long id);
		
	public CourseSchedule save(CourseScheduleRequest courseScheduleRequestData, MultipartFile studentsFile);
	
	public CourseSchedule update(Long id, CourseScheduleRequest courseScheduleRequestData, MultipartFile studentsFile);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
}