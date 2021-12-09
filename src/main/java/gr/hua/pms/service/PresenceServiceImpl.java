package gr.hua.pms.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.Presence;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.ManagePresencesRequest;
import gr.hua.pms.payload.request.PresenceRequest;
import gr.hua.pms.payload.response.PresenceResponse;
import gr.hua.pms.repository.ClassSessionRepository;
import gr.hua.pms.repository.PresenceRepository;

@Service
public class PresenceServiceImpl implements PresenceService {

	@Autowired
	PresenceRepository presenceRepository;
	
	@Autowired
	ClassSessionRepository classSessionRepository;
	
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
	public Map<String, Object> findAllByClassSessionIdAndStatusSortedPaginated(Long classSessionId, String status, String filter, int page,
			int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByClassSessionIdAndStatusSortedPaginated(classSessionId, typeOfStatusModerator(status), filter, pagingSort);
		
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
	public Map<String, Object> findAllByClassSessionIdStatusAndExcuseStatusSortedPaginated(Long classSessionId,
			String status, String excuseStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByClassSessionIdStatusAndExcuseStatusSortedPaginated(
				classSessionId, typeOfStatusModerator(status), typeOfStatusModerator(excuseStatus), filter, pagingSort);
		
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
	public Map<String, Object> findAllAbsencesByUserIdAndStatusSortedPaginated(Long userId, String status, String excuseStatus,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByUserIdStatusAndExcuseStatusSortedPaginated(userId, typeOfStatusModerator(status),
				typeOfStatusModerator(excuseStatus) ,filter, pagingSort);
		
		presences = pagePresences.getContent();

		if(presences.isEmpty()) {
			return null;
		}
				
		List<PresenceResponse> presencesResponse = createAbsencesResponse(presences);

		Map<String, Object> response = new HashMap<>();
		response.put("presences", presencesResponse);
		response.put("currentPage", pagePresences.getNumber());
		response.put("totalItems", pagePresences.getTotalElements());
		response.put("totalPages", pagePresences.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByUserIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByUserIdCourseScheduleIdAndTypeSortedPaginated(userId, courseScheduleId,
				lectureType,filter, pagingSort);
		
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
	public Map<String, Object> findAllByUserIdCourseScheduleIdTypeAndStatusSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String status, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByUserIdCourseScheduleIdTypeAndStatusSortedPaginated(userId, courseScheduleId, 
				lectureType, typeOfStatusModerator(status), filter, pagingSort);
		
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
	public Map<String, Object> findAllByAllParametersSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType lectureType, String status, String excuseStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Presence> presences = new ArrayList<Presence>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Presence> pagePresences = null;

		pagePresences = presenceRepository.searchByAllParametersSortedPaginated(userId, courseScheduleId,
				lectureType, typeOfStatusModerator(status), typeOfStatusModerator(excuseStatus), filter, pagingSort);
		
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
	public List<Presence> createPresences(ManagePresencesRequest managePresencesRequest) {
		ClassSession classSession = classSessionService.findById(managePresencesRequest.getClassSessionId());
		
		if (classSession == null) {
			return null;
		}
		
		return presenceRepository.saveAll(createPresences(classSession));
	}
	
	private List<Presence> createPresences(ClassSession classSession) {
		List<Presence> presences = new ArrayList<Presence>();
		classSession.getStudents().forEach(student -> {
			if (presenceRepository.searchByClassSessionIdAndStudentId(classSession.getId(), student.getId()) == null) {
				Presence presence = new Presence();
				presence.setStatus(null);
				presence.setExcuseStatus(null);
				presence.setClassSession(classSession);
				presence.setStudent(student);
				presence.setPresenceStatementDateTime(createPresenceTimestamp());
				presences.add(presence);
			}
		});
		return presences;
	}
	
	@Override
	public List<Presence> updatePresences(ManagePresencesRequest managePresencesRequest) {
		ClassSession classSession = classSessionService.findById(managePresencesRequest.getClassSessionId());
		System.out.println("Service Level Spot B: classSession"+classSession);
		if (classSession == null) {
			return null;
		}
		
		return presenceRepository.saveAll(updatePresences(classSession));
	}
	
	private List<Presence> updatePresences(ClassSession classSession) {
		List<Presence> presences = new ArrayList<Presence>();
		classSession.getStudents().forEach(student -> {
			Presence _presence = presenceRepository.searchByClassSessionIdAndStudentId(classSession.getId(), student.getId());
			System.out.println("Function Level Spot C: presence "+_presence);
			if (_presence != null) {
				Boolean status = _presence.getStatus();
				System.out.println("Function Level Spot D: presence "+status);
				if (status == null) {
					_presence.setStatus(false);
					_presence.setExcuseStatus(false);
					_presence.setPresenceStatementDateTime(createPresenceTimestamp());
					presences.add(_presence);
				}
			}
		});
		return presences;
	}
	
	@Override
	public Presence save(PresenceRequest presenceRequestData) {
		User student = userService.findById(presenceRequestData.getStudentId());
		ClassSession classSession = classSessionService.findById(presenceRequestData.getClassSessionId());
		
		if(presenceRepository.searchStudentByStudentId(student.getId()) == null) {
			throw new BadRequestDataException("Presence cannot be declared since student "+student.getUsername()+" does not exists "
					+ "in "+classSession.getNameIdentifier());
		}
		
		if (presenceRepository.searchByClassSessionIdAndStudentId(classSession.getId(), student.getId()) != null) {
			throw new BadRequestDataException("Presence for student "+student.getUsername()+" already exists");
		}
		
		Presence _presence = new Presence();
		
		_presence.setClassSession(classSession);
		_presence.setStudent(student);
		_presence.setStatus(presenceRequestData.getStatus());
		
		if (presenceRequestData.getStatus()) {
			_presence.setExcuseStatus(null);
		} else {
			_presence.setExcuseStatus(false);
		}
		
		_presence.setPresenceStatementDateTime(createPresenceTimestamp());
		
		Presence presence = presenceRepository.save(_presence);
		
		return presence;
	}
	
	@Override
	public LocalDateTime createPresenceTimestamp() {
		LocalDateTime now = LocalDateTime.now();
		
		LocalDateTime presenceDateTime = LocalDateTime.of(now.getYear(), 
				now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
		
		return presenceDateTime;
	}
	
	@Override
	public Presence update(Long id, PresenceRequest presenceRequestData) {
		Presence _presence = presenceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Presence with id = " + id));
		
		_presence.setStatus(presenceRequestData.getStatus());
		
		if (presenceRequestData.getStatus() == true) {
			_presence.setExcuseStatus(null);
		} else {
			_presence.setExcuseStatus(false);
		}
		
		return presenceRepository.save(_presence);
	}

	@Override
	public Presence updatePresenceStatus(PresenceRequest presenceRequestData) {
		if ((classSessionRepository.searchPresentedClassSessionByStudentIdAndStatus(presenceRequestData.getStudentId(), true)) != null) {
			throw new BadRequestDataException("You cannot make more than 1 presence statement for Lectures they are runnig in the same time");
		}
		Presence _presence = presenceRepository.searchByClassSessionIdAndStudentId(
				presenceRequestData.getClassSessionId(),
				presenceRequestData.getStudentId());
		
		if (_presence == null) {
			throw new BadRequestDataException("Not found Presence for student to update");
		}
		
		if (!presenceRequestData.getStatus()) {
			throw new BadRequestDataException("You cannot make an Absence statement !");
		}
		
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
	    
	    if (sort[0].matches("lecture")) {
	    	sort[0] = "classSession.lecture.nameIdentifier";
	    }
	    
	    if (sort[0].matches("session_date")) {
	    	sort[0] = "classSession.startDateTime";
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
	
	private Boolean typeOfStatusModerator(String typeOfStatus) {
	    System.out.println("SPOT D");
	    
	    System.out.println("Type Of Status: "+typeOfStatus);

	    if (typeOfStatus.matches("Present") || typeOfStatus.matches("Excused") || typeOfStatus.matches("true")) {
		    System.out.println("SPOT E");
	    	return true;
	    } else if (typeOfStatus.matches("Absent") || typeOfStatus.matches("Inexcusable") || typeOfStatus.matches("false")) {
		    System.out.println("SPOT F");
	    	return false;
	    } else {
		    System.out.println("SPOT G");
	    	return null;
	    }

	}
	
	public LocalDateTime createCurrentTimestamp() {
		LocalDateTime now = LocalDateTime.now();
		
		LocalDateTime currentDateTime = LocalDateTime.of(now.getYear(), 
				now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
		
		return currentDateTime;
	}
	
	public String formatter(LocalDateTime localDateTime) {
		
        String formatPattern = "yyyy/MM/dd HH:mm:ss";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(formatPattern);
        System.out.println(dateFormatter.format(LocalDateTime.of(localDateTime.getYear(), 
				localDateTime.getMonth(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond())));
		
		
		return dateFormatter.format(LocalDateTime.of(localDateTime.getYear(), 
				localDateTime.getMonth(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond()));
	}
	
	@Override
	public List<PresenceResponse> createPresencesResponse(List<Presence> presences) {
		List<PresenceResponse> presencesResponse = new ArrayList<PresenceResponse>();
		
		presences.forEach(presence -> {
			PresenceResponse presenceResponse = 
					new PresenceResponse(
							presence.getId(),
							presence.getStatus(),
							presence.getExcuseStatus(),
							classSessionService.createClassSessionResponse(presence.getClassSession()),
							userService.createUserResponse(presence.getStudent()),
							presence.getPresenceStatementDateTime());
			presencesResponse.add(presenceResponse);
		});
		
		return presencesResponse;
	}
	
	@Override
	public List<PresenceResponse> createAbsencesResponse(List<Presence> presences) {
		List<PresenceResponse> presencesResponse = new ArrayList<PresenceResponse>();
		
		presences.forEach(presence -> {
			LocalDateTime currentTimestamp = createCurrentTimestamp();

	        SimpleDateFormat sdf = new SimpleDateFormat(
	            "yyyy/MM/dd HH:mm:ss");
	        
			try {
				Date d1 = sdf.parse (formatter(presence.getPresenceStatementDateTime()).toString());
				Date d2 = sdf.parse(formatter(currentTimestamp).toString());
				

				//differance in ms
				long differance = d2.getTime() - d1.getTime();
				long expirationDuration = ((3600 * 1000) * 48);
				
				System.out.println("Differance between: "+differance);
				System.out.println("Expiration duration: "+expirationDuration);
				
				if (expirationDuration > differance) {
					PresenceResponse presenceResponse = 
							new PresenceResponse(
									presence.getId(),
									presence.getStatus(),
									presence.getExcuseStatus(),
									classSessionService.createClassSessionResponse(presence.getClassSession()),
									userService.createUserResponse(presence.getStudent()),
									presence.getPresenceStatementDateTime());
					presencesResponse.add(presenceResponse);
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return presencesResponse;
	}
	
	@Override
	public PresenceResponse createPresenceResponse(Presence presence) {
		return new PresenceResponse(
				presence.getId(),
				presence.getStatus(),
				presence.getExcuseStatus(),
				classSessionService.createClassSessionResponse(presence.getClassSession()),
				userService.createUserResponse(presence.getStudent()),
				presence.getPresenceStatementDateTime());
	}

}