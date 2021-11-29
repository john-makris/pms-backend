package gr.hua.pms.service;

import java.time.LocalDateTime;
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

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.ClassSession;
import gr.hua.pms.model.Presence;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.PresenceRequest;
import gr.hua.pms.payload.response.PresenceResponse;
import gr.hua.pms.repository.PresenceRepository;

@Service
public class PresenceServiceImpl implements PresenceService {

	@Autowired
	PresenceRepository presenceRepository;
	
	@Autowired
	ClassSessionService classSessionService;
	
	@Autowired
	UserService userService;
	
	@Override
	public Map<String, Object> findAllByClassSessionIdSortedPaginated(Long classSessionId, String filter, int page,
			int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByClassSessionIdSortedPaginated(classSessionId, filter, pagingSort);
		
		presences = pagePresences.getContent();

		if(presences.isEmpty()) {
			return null;
		}
				
		List<PresenceResponse> presencesResponse = createPresencesResponse(presences);

		Map<String, Object> response = new HashMap<>();
		response.put("presences", presencesResponse);
		response.put("currentPage", pagePresences.getNumber());
		response.put("totalItems", pagePresences.getTotalElements());
		response.put("totalPages", pagePresences.getTotalPages());

		return response;
	}

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
		
		List<PresenceResponse> presencesResponse = createPresencesResponse(presences);
		
		Map<String, Object> response = new HashMap<>();
		response.put("presences", presencesResponse);
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
	public PresenceResponse findPresenceResponseById(Long id) throws IllegalArgumentException {
		Presence presence = presenceRepository.findById(id).orElse(null);
		return createPresenceResponse(presence);
	}
	
	@Override
	public Presence findById(long id) {
		Presence presence = presenceRepository.findById(id).orElse(null);
		return presence;
	}

	@Override
	public Presence save(PresenceRequest presenceRequestData) {
		User student = userService.findById(presenceRequestData.getStudentId());
		ClassSession classSession = classSessionService.findById(presenceRequestData.getClassSessionId());
		
		if(presenceRepository.searchStudentByStudentId(student.getId()) == null) {
			throw new BadRequestDataException("Presence cannot be declared since student "+student.getUsername()+" does not exists "
					+ "in "+classSession.getNameIdentifier());
		}
		
		if (!(presenceRepository.searchByClassSessionIdAndStudentId(classSession.getId(), student.getId()).isEmpty())) {
			throw new BadRequestDataException("Presence for student "+student.getUsername()+" already exists");
		}
		
		Presence _presence = new Presence();
		
		_presence.setClassSession(classSession);
		_presence.setStudent(student);
		_presence.setStatus(presenceRequestData.getStatus());
		
		LocalDateTime now = LocalDateTime.now();
				
		LocalDateTime presenceDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
		
		_presence.setPresenceStatementDateTime(presenceDateTime);
		
		Presence presence = presenceRepository.save(_presence);
		
		return presence;
	}
	
	@Override
	public Presence update(Long id, PresenceRequest presenceRequestData) {
		Presence _presence = presenceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Presence with id = " + id));
		
		_presence.setStatus(presenceRequestData.getStatus());
		
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

	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);
	    
	    if (sort[0].matches("username")) {
	    	sort[0] = "student.username";
	    }
	    
	    if (sort[0].matches("firstname")) {
	    	sort[0] = "student.firstname";
	    }
	    
	    if (sort[0].matches("lastname")) {
	    	sort[0] = "student.lastname";
	    }
	    
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
	
	public List<PresenceResponse> createPresencesResponse(List<Presence> presences) {
		List<PresenceResponse> presencesResponse = new ArrayList<PresenceResponse>();
		
		presences.forEach(presence -> {
			PresenceResponse presenceResponse = 
					new PresenceResponse(
							presence.getId(),
							presence.getStatus(),
							classSessionService.createClassSessionResponse(presence.getClassSession()),
							userService.createUserResponse(presence.getStudent()));
			presencesResponse.add(presenceResponse);
		});
		
		return presencesResponse;
	}
	
	public PresenceResponse createPresenceResponse(Presence presence) {
		return new PresenceResponse(
				presence.getId(),
				presence.getStatus(),
				classSessionService.createClassSessionResponse(presence.getClassSession()),
				userService.createUserResponse(presence.getStudent()));
	}

}