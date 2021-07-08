package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Order;

import gr.hua.pms.model.Presence;

public interface PresenceService {

	public Map<String, Object> findPresencesByPresenceStatusSorted(Boolean status, int page, int size, String[] sort);
	
	public List<Presence> findAll(String[] sort);
	
	public Presence findById(Long id);
	
	public Presence save(Presence presence);
	
	public Presence update(Long id, Presence presence);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<Order> createOrders(String[] sort);
}