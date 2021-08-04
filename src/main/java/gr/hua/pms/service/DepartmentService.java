package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.Department;

public interface DepartmentService {

	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);

	public Map<String, Object> findAllBySchoolIdSortedPaginated(Long id, String filter, int page, int size, String[] sort);
	
	public List<Department> findAll(String[] sort);
	
	public Department findById(Long id);
	
	public Department save(Department department);
	
	public Department update(Long id, Department department);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
}