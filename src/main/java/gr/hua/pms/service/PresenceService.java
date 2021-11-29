package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.Presence;
import gr.hua.pms.payload.request.PresenceRequest;
import gr.hua.pms.payload.response.PresenceResponse;

public interface PresenceService {

	public Map<String, Object> findAllByClassSessionIdSortedPaginated(Long classSessionId,
			String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findPresencesByPresenceStatusSorted(Boolean status, int page, int size, String[] sort);
	
	public Presence findById(long id);
	
	Presence save(PresenceRequest presenceRequestData);
	
	Presence update(Long id, PresenceRequest presenceRequestData);
	
	public List<Presence> findAll(String[] sort);
	
	public PresenceResponse findPresenceResponseById(Long id);
			
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);

}