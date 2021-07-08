package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.School;

public interface SchoolService {

	public Map<String, Object> findAllSorted(String name, int page, int size, String[] sort);

	public List<School> findAll(String[] sort);
	
	public School findById(Long id);
	
	public School save(School school);
	
	public School update(Long id, School school);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);
	
}