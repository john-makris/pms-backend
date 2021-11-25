package gr.hua.pms.service;

import java.util.Map;

import gr.hua.pms.model.ClassSession;
import gr.hua.pms.payload.request.ClassSessionRequest;
import gr.hua.pms.payload.response.ClassSessionResponse;

public interface ClassSessionService {
	
	public Map<String, Object> findAllByLectureIdAndClassGroupIdSortedPaginated(Long lectureId, 
			Long classGroupId, String filter, int page, int size, String[] sort);
	
	public ClassSession save(ClassSessionRequest classSessionRequestData);

	ClassSessionResponse findById(Long id);

	ClassSession update(Long id, ClassSessionRequest classSessionRequestData);

	void deleteById(Long id);

}
