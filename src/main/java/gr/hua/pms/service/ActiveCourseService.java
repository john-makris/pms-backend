package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.ActiveCourse;

public interface ActiveCourseService {

	public Map<String, Object> findAllSorted(String name, int page, int size, String[] sort);
		
	public List<ActiveCourse> findAll(String[] sort);
	
	public ActiveCourse findById(Long id);
	
	public ActiveCourse save(ActiveCourse activeCourse);
	
	public ActiveCourse update(Long id, ActiveCourse activeCourse);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);
}