package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.School;

public interface SchoolService {

	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);

	public List<School> findAll(String[] sort);
	
	public School findById(Long id);
	
	public School save(School school);
	
	public School update(Long id, School school);
	
	public void deleteById(Long id);
	
	public void deleteAll();
		
}