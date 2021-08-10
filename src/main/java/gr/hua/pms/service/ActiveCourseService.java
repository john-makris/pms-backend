package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ActiveCourse;

public interface ActiveCourseService {

	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
		
	public Map<String, Object> findAllByCourseIdSortedPaginated(Long id, String filter, int page, int size, String[] sort);

	public List<ActiveCourse> findAll(String[] sort);
	
	public ActiveCourse findById(Long id);
	
	public ActiveCourse findByCourseId(Long id);
	
	public ActiveCourse save(ActiveCourse activeCourse);
	
	public ActiveCourse update(Long id, ActiveCourse activeCourse);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
}