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

	public Map<String, Object> findAllByClassSessionIdSortedPaginated(Long classSessionId,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByClassSessionIdAndStatusSortedPaginated(Long classSessionId, String status,
			String filter, int page, int size, String[] sort);
	
	Map<String, Object> findAllByClassSessionIdStatusAndExcuseStatusSortedPaginated(Long classSessionId, String status,
			String excuseStatus, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllAbsencesByUserIdAndStatusSortedPaginated(Long userId,
			String status, String excuseStatus, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findPresencesByPresenceStatusSorted(Boolean status, int page, int size, String[] sort);
	
	public Presence findById(long id);
	
	Presence save(PresenceRequest presenceRequestData);
	
	Presence update(Long id, PresenceRequest presenceRequestData);
	
	public List<Presence> findAll(String[] sort);
	
	public PresenceResponse findPresenceResponseById(Long id);
			
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);

	LocalDateTime createPresenceTimestamp();

	List<Presence> createPresences(ManagePresencesRequest managePresencesRequest);

	List<Presence> updatePresences(ManagePresencesRequest managePresencesRequest);

	Presence updatePresenceStatus(PresenceRequest presenceRequestData);

	List<PresenceResponse> createPresencesResponse(List<Presence> presences);

	PresenceResponse createPresenceResponse(Presence presence);

	List<PresenceResponse> createAbsencesResponse(List<Presence> presences);

}