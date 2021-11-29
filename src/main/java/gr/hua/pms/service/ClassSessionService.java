package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ClassSession;
import gr.hua.pms.payload.request.ClassSessionRequest;
import gr.hua.pms.payload.response.ClassSessionResponse;

public interface ClassSessionService {
	
	public Map<String, Object> findAllByLectureIdSortedPaginated(Long lectureId,
			String filter, int page, int size, String[] sort);
	
	public ClassSession save(ClassSessionRequest classSessionRequestData);

	ClassSessionResponse findClassSessionResponseById(Long id);

	ClassSession update(Long id, ClassSessionRequest classSessionRequestData);

	void deleteById(Long id);

	List<ClassSessionResponse> createClassesSessionsResponse(List<ClassSession> classesSessions);

	ClassSessionResponse createClassSessionResponse(ClassSession classSession);

	ClassSession findById(Long id);

}