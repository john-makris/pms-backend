package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.Presence;
import gr.hua.pms.repository.PresenceRepository;

@Service
public class PresenceServiceImpl implements PresenceService {

	@Autowired
	PresenceRepository presenceRepository;

	@Override
	public Map<String, Object> findPresencesByPresenceStatusSorted(Boolean status, int page, int size, String[] sort) {

		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;
		
		if(status==null) {
			try {
				pagePresences = presenceRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			pagePresences = presenceRepository.findByStatusContaining(status, pagingSort);
			System.out.println("3 "+pagePresences);
		}
		
		presences = pagePresences.getContent();

		if(presences.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("presences", presences);
		response.put("currentPage", pagePresences.getNumber());
		response.put("totalItems", pagePresences.getTotalElements());
		response.put("totalPages", pagePresences.getTotalPages());

		return response;
	}

	@Override
	public List<Presence> findAll(String[] sort) throws IllegalArgumentException {
		try {
			return presenceRepository.findAll(Sort.by(createOrders(sort)));
		}catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Presence findById(Long id) throws IllegalArgumentException {
		Presence presence = presenceRepository.findById(id).orElse(null);
		return presence;
	}

	@Override
	public Presence save(Presence presence) throws IllegalArgumentException {
		return presenceRepository.save(presence);
	}

	@Override
	public Presence update(Long id, Presence presence) {
		Presence _presence = presenceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Presence with id = " + id));
		
		//_presence.setLecture(presence.getLecture());
		//_presence.setPresenceDate(presence.getPresenceDate());
		//_presence.setPresenceStatus(_presence.getPresenceStatus());
		_presence.setStudent(presence.getStudent());
		
		return presenceRepository.save(_presence);
	}

	@Override
	public void deleteById(Long id) throws IllegalArgumentException {
		Presence presence = presenceRepository.findById(id).orElse(null);
		if(presence!=null) {
			presenceRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		presenceRepository.deleteAll();
	}

	@Override
	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    if (sort[0].contains(",")) {
          // will sort more than 2 fields
          // sortOrder="field, direction"
          for (String sortOrder : sort) {
            String[] _sort = sortOrder.split(",");
            orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
          }
        } else {
          // sort=[field, direction]
          orders.add(new Order(getSortDirection(sort[1]), sort[0]));
        }
	        
	  	return orders;
	}
	
	private Sort.Direction getSortDirection(String direction) {
		  if (direction.equals("asc")) {
			  return Sort.Direction.ASC;
		  } else if (direction.equals("desc")) {
			  return Sort.Direction.DESC;
		  }
			  return Sort.Direction.ASC;
	}

}