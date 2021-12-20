package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ClassSession;
import gr.hua.pms.payload.request.ClassSessionRequest;
import gr.hua.pms.payload.response.ClassSessionResponse;

public interface ClassSessionService {
	
	public Map<String, Object> findAllByLectureIdSortedPaginated(Long userId, Long lectureId,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByUserIdAndStatusSortedPaginated(Long userId, Boolean status, String filter,
			int page, int size, String[] sort);
	
	public Map<String, Object> findAllByLectureIdAndStatusSortedPaginated(Long userId, Long lectureId, String status, String filter,
			int page, int size, String[] sort);
	
	public ClassSession save(ClassSessionRequest classSessionRequestData, Long userId);

	ClassSessionResponse findClassSessionResponseById(Long id, Long userId);
	
	ClassSessionResponse findClassSessionResponseByLectureIdAndStudentId(Long lectureId, Long studentId);

	ClassSession update(Long id, Long userId, ClassSessionRequest classSessionRequestData);
	
	void deleteById(Long id, Long userId);

	List<ClassSessionResponse> createClassesSessionsResponse(List<ClassSession> classesSessions);

	ClassSessionResponse createClassSessionResponse(ClassSession classSession);

	ClassSession findById(Long id);

	public ClassSessionResponse findPresentedClassSessionResponseByStudentIdAndStatus(Long studentId, Boolean status);

}