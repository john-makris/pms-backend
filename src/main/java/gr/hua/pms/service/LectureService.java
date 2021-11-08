package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.Lecture;
import gr.hua.pms.payload.request.LectureRequest;
import gr.hua.pms.payload.response.LectureResponse;

public interface LectureService {
	
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByCourseScheduleIdSortedPaginated(Long courseScheduleId, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentSortedPaginated(Long departmentId, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentAndCourseScheduleIdSortedPaginated(Long departmentId, Long courseScheduleId, String filter, int page, int size, String[] sort);

	public Map<String, Object> findAllByDepartmentAndCourseScheduleIdPerTypeSortedPaginated(Long departmentId, Long courseScheduleId, 
			ELectureType name, String filter, int page, int size, String[] sort);
	
	public List<Lecture> findAll(String[] sort);
	
	public LectureResponse findById(Long id);
	
	public Lecture save(LectureRequest lecture);
	
	public Lecture update(Long id, LectureRequest lecture);
	
	public void deleteById(Long id);
	
	public void deleteAll();
}