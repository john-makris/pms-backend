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
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.ExcuseApplication;
import gr.hua.pms.model.Presence;
import gr.hua.pms.payload.request.ExcuseApplicationRequest;
import gr.hua.pms.payload.response.ExcuseApplicationResponse;
import gr.hua.pms.repository.ExcuseApplicationRepository;

@Service
public class ExcuseApplicationServiceImpl implements ExcuseApplicationService {

	@Autowired
	ExcuseApplicationRepository excuseApplicationRepository;
	
	@Autowired
	PresenceService presenseService;
	
	
	@Override
	public ExcuseApplication save(ExcuseApplicationRequest excuseApplicationRequestData) {
		Presence absence = presenseService.findById(excuseApplicationRequestData.getAbsenceId());
		
		LocalDateTime currentTimestamp = createCurrentTimestamp();

        SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm:ss");
                
		try {
			Date d1 = sdf.parse (formatter(absence.getPresenceStatementDateTime()).toString());
			Date d2 = sdf.parse(formatter(currentTimestamp).toString());
			

			//differance in ms
			long differance = d2.getTime() - d1.getTime();
			long expirationDuration = ((3600 * 1000) * 48);
			
			System.out.println("Differance between: "+differance);
			System.out.println("Expiration duration: "+expirationDuration);
			
			if (expirationDuration <= differance) {
				throw new BadRequestDataException("You can make an excuse application within 48 hours after the absence");
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (excuseApplicationRepository.searchByPresenceId(absence.getId()) != null) {
			throw new BadRequestDataException("You have already made an excuse application for "+
			absence.getClassSession().getLecture().getCourseSchedule().getCourse().getName()+", "
			+absence.getClassSession().getLecture().getNameIdentifier()+" absence");
		}
		
		ExcuseApplication _excuseApplication = new ExcuseApplication();

		_excuseApplication.setAbsence(absence);
		_excuseApplication.setStatus(null);	
		_excuseApplication.setDateTime(createCurrentTimestamp());
		
		ExcuseApplication excuseApplication = excuseApplicationRepository.save(_excuseApplication);
		
		return excuseApplication;
	}
	
	@Override
	public ExcuseApplication update(Long id, ExcuseApplicationRequest excuseApplicationRequestData) {
		ExcuseApplication _excuseApplication = excuseApplicationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Presence with id = " + id));
		
		_excuseApplication.setStatus(excuseApplicationRequestData.getStatus());
		
		return excuseApplicationRepository.save(_excuseApplication);
	}
	
	@Override
	public void deleteById(Long id) {
		ExcuseApplication excuseApplication = excuseApplicationRepository.findById(id).orElse(null);
		
		if(excuseApplication!=null) {
			/*if (groupStudentRepository.existsByClassGroupId(classSession.getId())) {
				throw new ResourceCannotBeDeletedException("You cannot delete "+
			classSession.getGroupType().getName().toString().toLowerCase()+"_"
						+classSession.getNameIdentifier()+" of "+classSession.getCourseSchedule().getCourse().getName()+" schedule"+
						", "+"since it has student subscriptions");
			} else {*/
			excuseApplicationRepository.deleteById(id);
			/*}*/
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public ExcuseApplicationResponse findExcuseApplicationResponseById(Long id) {
		ExcuseApplication excuseApplication = excuseApplicationRepository.findById(id).orElse(null);
		return createExcuseApplicationResponse(excuseApplication);
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long departmentId, String filter, int page,
			int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdSortedPaginated(departmentId, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentIdAndTypeSortedPaginated(Long departmentId, ELectureType name,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdAndTypeSortedPaginated(departmentId, name, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdAndStatusSortedPaginated(Long departmentId, String typeOfStatus,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

	    System.out.println("SPOT B");
		
		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
	    System.out.println("SPOT C");

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		System.out.println("Status: "+typeOfStatusModerator(typeOfStatus));

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdAndStatusSortedPaginated(departmentId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdTypeAndStatusSortedPaginated(Long departmentId, ELectureType name,
			String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdTypeAndStatusSortedPaginated(departmentId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(Long departmentId,
			Long courseScheduleId, String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(departmentId, courseScheduleId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByCompleteSearchSortedPaginated(Long departmentId, Long courseScheduleId,
			ELectureType name, String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.completeSearch(departmentId, courseScheduleId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentIdAndCourseScheduleIdSortedPaginated(Long departmentId,
			Long courseScheduleId, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdAndCourseScheduleIdSortedPaginated(departmentId, courseScheduleId, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(Long departmentId,
			Long courseScheduleId, ELectureType name, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(departmentId, courseScheduleId, name, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserIdAndStatusSortedPaginated(Long userId, String typeOfStatus, String filter,
			int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdAndStatusSortedPaginated(userId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}
	
	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);
	    
	    System.out.println("SPOT A");
	    
	    if (sort[0].matches("course")) {
	    	sort[0] = "absence.classSession.lecture.courseSchedule.course.name";
	    }
	    
	    if (sort[0].matches("lecture")) {
	    	sort[0] = "absence.classSession.lecture.nameIdentifier";
	    }
	    
	    if (sort[0].matches("dateTime")) {
	    	sort[0] = "absence.classSession.startDateTime";
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

	    if (typeOfStatus.matches("Approved")) {
		    System.out.println("SPOT E");
	    	return true;
	    } else if (typeOfStatus.matches("Rejected")) {
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
	public List<ExcuseApplicationResponse> createExcuseApplicationsResponse(List<ExcuseApplication> excuseApplications) {
		List<ExcuseApplicationResponse> excuseApplicationsResponse = new ArrayList<ExcuseApplicationResponse>();
		
		excuseApplications.forEach(excuseApplication -> {
			ExcuseApplicationResponse excuseApplicationResponse = 
					new ExcuseApplicationResponse(
							excuseApplication.getId(),
							presenseService.createPresenceResponse(excuseApplication.getAbsence()),
							excuseApplication.getStatus(),
							excuseApplication.getDateTime());
			excuseApplicationsResponse.add(excuseApplicationResponse);
		});
		
		return excuseApplicationsResponse;
	}
	
	@Override
	public ExcuseApplicationResponse createExcuseApplicationResponse(ExcuseApplication excuseApplication) {
		return new ExcuseApplicationResponse(
							excuseApplication.getId(),
							presenseService.createPresenceResponse(excuseApplication.getAbsence()),
							excuseApplication.getStatus(),
							excuseApplication.getDateTime());
	}

}