package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.Course;

public interface CourseService {

	public Map<String, Object> findAllSortedPaginated(String name, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long id, String name, int page, int size, String[] sort);

	public List<Course> findAll(String[] sort);
	
	public Course findById(Long id);
	
	public Course save(Course course);
	
	public Course update(Long id, Course course);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);

}