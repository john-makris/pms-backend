package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.Lecture;

public interface LectureService {
	
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByCourseScheduleIdSortedPaginated(Long courseScheduleId, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentSortedPaginated(Long departmentId, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentAndCourseScheduleIdSortedPaginated(Long departmentId, Long courseScheduleId, String filter, int page, int size, String[] sort);

	public List<Lecture> findAll(String[] sort);
	
	public Lecture findById(Long id);
	
	public Lecture save(Lecture lecture);
	
	public Lecture update(Long id, Lecture lecture);
	
	public void deleteById(Long id);
	
	public void deleteAll();
}