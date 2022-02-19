package gr.hua.pms.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.Presence;
import gr.hua.pms.payload.request.ManagePresencesRequest;
import gr.hua.pms.payload.request.PresenceRequest;
import gr.hua.pms.payload.response.PresenceResponse;

public interface PresenceService {

	public Map<String, Object> findAllByClassSessionIdSortedPaginated(Long userId, Long classSessionId,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByClassSessionIdAndStatusSortedPaginated(Long userId, Long classSessionId, String status,
			String filter, int page, int size, String[] sort);
	
	Map<String, Object> findAllByClassSessionIdStatusAndExcuseStatusSortedPaginated(Long userId, Long classSessionId, String status,
			String excuseStatus, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllAbsencesByUserIdAndStatusSortedPaginated(Long currentUserId, Long userId,
			String status, String excuseStatus, String filter, int page, int size, String[] sort);
	
	// public Map<String, Object> findPresencesByPresenceStatusSorted(Boolean status, int page, int size, String[] sort);
	
	/*public Map<String, Object> findAllByUserIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String filter, int page, int size, String[] sort);
	
	Map<String, Object> findAllByUserIdCourseScheduleIdTypeAndStatusSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String status, String filter, int page, int size, String[] sort);
	
	Map<String, Object> findAllByAllParametersSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String status, String excuseStatus, String filter, int page, int size, String[] sort); */
	
	public Presence findById(long id);
	
	Presence save(PresenceRequest presenceRequestData);
	
	Presence update(Long userId, Long id, PresenceRequest presenceRequestData);
	
	public List<Presence> findAll(String[] sort);
	
	public PresenceResponse findPresenceResponseById(Long id, Long userId);
			
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);

	LocalDateTime createPresenceTimestamp();

	List<Presence> createPresences(ManagePresencesRequest managePresencesRequest, Long userId);

	List<Presence> updatePresences(Long id, Long userId);

	Presence updatePresenceStatus(PresenceRequest presenceRequestData);

	List<PresenceResponse> createPresencesResponse(List<Presence> presences, Long currentUserId);

	PresenceResponse createPresenceResponse(Presence presence, Long currentUserId);

	List<Presence> updateClassesSessionsPresences(Long id, Long userId);

}