package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.Lecture;

public interface LectureService {

	public Map<String, Object> findAllSorted(Boolean status, int page, int size, String[] sort);
	
	public List<Lecture> findAll(String[] sort);
	
	public Lecture findById(Long id);
	
	public Lecture save(Lecture lecture);
	
	public Lecture update(Long id, Lecture lecture);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);
}
