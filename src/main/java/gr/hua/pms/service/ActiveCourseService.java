package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.model.ActiveCourse;
import gr.hua.pms.payload.request.ActiveCourseRequest;

public interface ActiveCourseService {

	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
		
	public Map<String, Object> findAllByCourseIdSortedPaginated(Long id, String filter, int page, int size, String[] sort);

	public List<ActiveCourse> findAll(String[] sort);
	
	public ActiveCourse findById(Long id);
	
	public ActiveCourse findByCourseId(Long id);
		
	public ActiveCourse save(ActiveCourseRequest activeCourseData, MultipartFile studentsFile);
	
	public ActiveCourse update(Long id, ActiveCourseRequest activeCourseData, MultipartFile studentsFile);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
}