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
import gr.hua.pms.model.ERole;
import gr.hua.pms.model.ExcuseApplication;
import gr.hua.pms.model.Presence;
import gr.hua.pms.payload.request.ExcuseApplicationRequest;
import gr.hua.pms.payload.response.ExcuseApplicationResponse;
import gr.hua.pms.repository.ExcuseApplicationRepository;
import gr.hua.pms.repository.PresenceRepository;

@Service
public class ExcuseApplicationServiceImpl implements ExcuseApplicationService {

	@Autowired
	ExcuseApplicationRepository excuseApplicationRepository;
	
	@Autowired
	PresenceRepository presenceRepository;
	
	@Autowired
	PresenceService presenceService;
	
	@Autowired
	UserService userService;
	
	@Override
	public ExcuseApplication save(ExcuseApplicationRequest excuseApplicationRequestData, Long userId) {
		Presence absence = presenceService.findById(excuseApplicationRequestData.getAbsenceId());
		
		if (!userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are not admin");
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_STUDENT)) {
				System.out.println("You are student");
				if ((presenceRepository.checkStudentOwnershipByPresenceId(excuseApplicationRequestData.getAbsenceId())) == null) {
					throw new BadRequestDataException("You cannot make an excuse application,"
							+ " for an absence it's not yours");
				}
			}
		}
		
		checkAbsenceValidity(absence);
		
		List<Presence> inExcusableAbsences = presenceRepository.searchAbsencesByExcuseStatusAndCourseSchedule(absence.getStudent().getId(), false,
				absence.getClassSession().getLecture().getCourseSchedule().getId(), absence.getClassSession().getLecture().getLectureType().getName());
		// check if student has 2 expired inexcusable absences
		if (inExcusableAbsences != null) {
			List<Presence> unableToExcuseAbsences = unableToExcuseAbsencesFilter(inExcusableAbsences);
			System.out.println("unableToExcuseAbsences "+unableToExcuseAbsences.size());
			if (unableToExcuseAbsences.size() > 2) {
				throw new BadRequestDataException(" you cannot make an excuse application for "
			+absence.getClassSession().getLecture().getCourseSchedule().getCourse().getName()+" "
			+(absence.getClassSession().getLecture().getLectureType().getName().equals(ELectureType.Theory) ? "theories" : "labs")
			+" since you have more than 2 inexcusable absences !");
			}
		}
		
		List<Presence> excusedAbsences = presenceRepository.searchAbsencesByExcuseStatusAndCourseSchedule(absence.getStudent().getId(), true,
				absence.getClassSession().getLecture().getCourseSchedule().getId(), absence.getClassSession().getLecture().getLectureType().getName());
		
		if (excusedAbsences != null) {
			if (excusedAbsences.size() >= excuseAbsencesLimitCalculator(absence)) {
				throw new BadRequestDataException(" you cannot make an excuse application for "
			+absence.getClassSession().getLecture().getCourseSchedule().getCourse().getName()+" "
			+(absence.getClassSession().getLecture().getLectureType().getName().equals(ELectureType.Theory) ? "theories" : "labs")
			+" since you have already excuse "+excuseAbsencesLimitCalculator(absence)+" absences !");
			}
		}
		
		checkAbsenceExcusability(absence);
		
		if (excuseApplicationRepository.searchByPresenceId(absence.getId()) != null) {
			throw new BadRequestDataException("You have already made an excuse application for "+
			absence.getClassSession().getLecture().getCourseSchedule().getCourse().getName()+", "
			+absence.getClassSession().getLecture().getNameIdentifier()+" absence");
		}
		
		ExcuseApplication _excuseApplication = new ExcuseApplication();

		_excuseApplication.setAbsence(absence);
		_excuseApplication.setStatus(null);	
		_excuseApplication.setDateTime(createCurrentTimestamp());
		_excuseApplication.setReason(excuseApplicationRequestData.getReason());
		
		ExcuseApplication excuseApplication = excuseApplicationRepository.save(_excuseApplication);
		
		return excuseApplication;
	}
	
	private void checkAbsenceValidity(Presence absence) {
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
					throw new BadRequestDataException("You cannot make an axcuse application for an absence that exists over 48 hours");
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private int excuseAbsencesLimitCalculator(Presence absence) {
		int numOfExcusableLectures = 0;
		int numberOfLectures = 0;
		ELectureType lectureType = absence.getClassSession().getLecture().getLectureType().getName();
		
		System.out.println("lectureType "+lectureType);

		if (lectureType.equals(ELectureType.Theory)) {
			numberOfLectures = absence.getClassSession().getLecture().getCourseSchedule().getMaxTheoryLectures();
		} else {
			numberOfLectures = absence.getClassSession().getLecture().getCourseSchedule().getMaxLabLectures();
		}
		
		System.out.println("numberOfLectures "+numberOfLectures);

		if (numberOfLectures % 2 != 0) {
			System.out.println("Odd number");
			if (numberOfLectures == 1) {
				numOfExcusableLectures = 1;
			} else {
				numOfExcusableLectures = ((numberOfLectures + 1) / 2) - 1;
			}
		} else {
			System.out.println("Even number");
			numOfExcusableLectures = numberOfLectures / 2;
		}
		
		System.out.println("numOfExcusableLectures "+numOfExcusableLectures);
		
		return numOfExcusableLectures;
	}
	
	private List<Presence> unableToExcuseAbsencesFilter(List<Presence> absences) {
		List<Presence> unableToExcuseAbsences = new ArrayList<>();
		absences.forEach(absence -> {
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
					unableToExcuseAbsences.add(absence);
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return unableToExcuseAbsences;
	}
	
	@Override
	public ExcuseApplication update(Long id, Long userId, ExcuseApplicationRequest excuseApplicationRequestData) {
		ExcuseApplication _excuseApplication = excuseApplicationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Presence with id = " + id));
		
		if (!userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are not admin");
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				if ((excuseApplicationRepository.checkSecretaryOwnershipByExcuseApplicationId(_excuseApplication.getId())) == null) {
					throw new BadRequestDataException("You cannot make an excuse application,"
							+ " for an absence it's not yours");
				}
			}
		}
		
		if (!excuseApplicationEvaluationValidator(_excuseApplication.getDateTime())) {
			throw new BadRequestDataException("You can evaluate every excuse application within 30 days of the statement");
		}
		
		_excuseApplication.setStatus(excuseApplicationRequestData.getStatus());
		
		Presence presence = presenceService.findById(excuseApplicationRequestData.getAbsenceId());
		
		if (presence != null) {
			if (excuseApplicationRequestData.getStatus() == true) {
				presence.setExcuseStatus(true);
			} else {
				presence.setExcuseStatus(false);
			}
			presenceRepository.save(presence);
		}
		
		return excuseApplicationRepository.save(_excuseApplication);
	}
	
	private boolean excuseApplicationEvaluationValidator(LocalDateTime excuseApplicationDateTime) {
		LocalDateTime deadline = excuseApplicationDateTime.plusDays(30);
		LocalDateTime currentDayAndTime = createCurrentTimestamp();
		
		System.out.println("Excuse application date and time of creation: ");
		System.out.println("Year: "+excuseApplicationDateTime.getYear());
		System.out.println("Month: "+excuseApplicationDateTime.getMonth());
		System.out.println("Day: "+excuseApplicationDateTime.getDayOfMonth());
		System.out.println("Hour: "+excuseApplicationDateTime.getHour());
		System.out.println("Minute: "+excuseApplicationDateTime.getMinute());
		System.out.println("Second: "+excuseApplicationDateTime.getSecond());

		System.out.println("Deadline date and time: ");
		System.out.println("Year: "+deadline.getYear());
		System.out.println("Month: "+deadline.getMonth());
		System.out.println("Day: "+deadline.getDayOfMonth());
		System.out.println("Hour: "+deadline.getHour());
		System.out.println("Minute: "+deadline.getMinute());
		System.out.println("Second: "+deadline.getSecond());
		
		if (currentDayAndTime.isAfter(deadline)) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void deleteById(Long id) {
		ExcuseApplication excuseApplication = excuseApplicationRepository.findById(id).orElse(null);
		
		if(excuseApplication!=null) {
			excuseApplicationRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public ExcuseApplicationResponse findExcuseApplicationResponseById(Long id, Long userId) {
		ExcuseApplication excuseApplication = new ExcuseApplication();
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are Admin");
			excuseApplication = excuseApplicationRepository.findById(id).orElse(null);
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are Teacher");
				excuseApplication = excuseApplicationRepository.checkSecretaryOwnershipByExcuseApplicationId(id);
				if (excuseApplication == null) {
					throw new BadRequestDataException("You don't have view privilages for this application, since you are not the owner");
				}
			}
			
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_STUDENT)) {
				System.out.println("You are Student");
				excuseApplication = excuseApplicationRepository.checkStudentOwnershipByExcuseApplicationId(id);
				if (excuseApplication == null) {
					throw new BadRequestDataException("You don't have view privilages for this application, since you are not the owner");
				}
			}
		}
		return createExcuseApplicationResponse(excuseApplication, userId);
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long userId, Long departmentId, String filter, int page,
			int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdSortedPaginated(departmentId, filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdSortedPaginated(departmentId, filter, pagingSort);
			}
		}

		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentIdAndTypeSortedPaginated(Long userId, Long departmentId, ELectureType name,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdAndTypeSortedPaginated(departmentId, name, filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdAndTypeSortedPaginated(departmentId, name, filter, pagingSort);
			}
		}

		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdAndStatusSortedPaginated(Long userId, Long departmentId, String typeOfStatus,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

	    System.out.println("SPOT B");
		
		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdAndStatusSortedPaginated(departmentId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdAndStatusSortedPaginated(departmentId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
			}
		}
		
	    System.out.println("SPOT C");

		System.out.println("Status: "+typeOfStatusModerator(typeOfStatus));

		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdTypeAndStatusSortedPaginated(Long userId, Long departmentId, ELectureType name,
			String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdTypeAndStatusSortedPaginated(departmentId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdTypeAndStatusSortedPaginated(departmentId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
			}
		}
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdCourseScheduleIdAndStatusSortedPaginated(departmentId, courseScheduleId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdCourseScheduleIdAndStatusSortedPaginated(departmentId, courseScheduleId, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
			}
		}
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByCompleteSearchSortedPaginated(Long userId, Long departmentId, Long courseScheduleId,
			ELectureType name, String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.completeSearch(departmentId, courseScheduleId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.completeOwnerSearch(departmentId, courseScheduleId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
			}
		}
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentIdAndCourseScheduleIdSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdAndCourseScheduleIdSortedPaginated(departmentId, courseScheduleId, filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdAndCourseScheduleIdSortedPaginated(departmentId, courseScheduleId, filter, pagingSort);
			}
		}

		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long departmentId,
			Long courseScheduleId, ELectureType name, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageExcuseApplications = excuseApplicationRepository.searchByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(departmentId, courseScheduleId, name, filter, pagingSort);
		
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_SECRETARY)) {
				System.out.println("You are secretary");
				pageExcuseApplications = excuseApplicationRepository.searchByOwnerDepartmentIdCourseScheduleIdAndTypeSortedPaginated(departmentId, courseScheduleId, name, filter, pagingSort);
			}
		}

		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

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
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

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
	
	private void checkAbsenceExcusability(Presence absence) {
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
	}
	
	private void updateExcuseApplicationStatus(ExcuseApplication excuseApplication) {
		if (excuseApplication.getStatus() == null) {
			excuseApplication.setStatus(false);
		}
	}
	
	@Override
	public List<ExcuseApplicationResponse> createExcuseApplicationsResponse(List<ExcuseApplication> excuseApplications, Long currentUserId) {
		List<ExcuseApplicationResponse> excuseApplicationsResponse = new ArrayList<ExcuseApplicationResponse>();
		
		excuseApplications.forEach(excuseApplication -> {
			
			if (!excuseApplicationEvaluationValidator(excuseApplication.getDateTime())) {
				updateExcuseApplicationStatus(excuseApplication);
			}
			
			ExcuseApplicationResponse excuseApplicationResponse = 
					new ExcuseApplicationResponse(
							excuseApplication.getId(),
							presenceService.createPresenceResponse(excuseApplication.getAbsence(), currentUserId),
							excuseApplication.getReason(),
							excuseApplication.getStatus(),
							excuseApplication.getDateTime());
			excuseApplicationsResponse.add(excuseApplicationResponse);
		});
		
		return excuseApplicationsResponse;
	}
	
	@Override
	public ExcuseApplicationResponse createExcuseApplicationResponse(ExcuseApplication excuseApplication, Long currentUserId) {
		if (!excuseApplicationEvaluationValidator(excuseApplication.getDateTime())) {
			updateExcuseApplicationStatus(excuseApplication);
		}
		
		return new ExcuseApplicationResponse(
							excuseApplication.getId(),
							presenceService.createPresenceResponse(excuseApplication.getAbsence(), currentUserId),
							excuseApplication.getReason(),
							excuseApplication.getStatus(),
							excuseApplication.getDateTime());
	}

	@Override
	public Map<String, Object> findAllByUserIdSortedPaginated(Long userId, String filter, int page, int size,
			String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdSortedPaginated(userId, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserIdAndCourseScheduleIdSortedPaginated(Long userId, Long courseScheduleId,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdAndCourseScheduleIdSortedPaginated(userId, courseScheduleId, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserIdCourseScheduleIdAndTypeSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType name, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdCourseScheduleIdAndTypeSortedPaginated(userId, courseScheduleId, name,
				filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserIdAndTypeSortedPaginated(Long userId, ELectureType name, String filter,
			int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdAndTypeSortedPaginated(userId, name, filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserIdTypeAndStatusSortedPaginated(Long userId, ELectureType name,
			String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdTypeAndStatusSortedPaginated(userId, name, typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserIdCourseScheduleIdAndStatusSortedPaginated(Long userId,
			Long courseScheduleId, String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.searchByUserIdCourseScheduleIdAndStatusSortedPaginated(userId, courseScheduleId,
				typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByUserCompleteSearchSortedPaginated(Long userId, Long courseScheduleId,
			ELectureType name, String typeOfStatus, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ExcuseApplication> excuseApplications = new ArrayList<ExcuseApplication>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ExcuseApplication> pageExcuseApplications = null;

		pageExcuseApplications = excuseApplicationRepository.completeByUserSearch(userId, courseScheduleId, name,
				typeOfStatusModerator(typeOfStatus), filter, pagingSort);
		
		excuseApplications = pageExcuseApplications.getContent();

		if(excuseApplications.isEmpty()) {
			return null;
		}
				
		List<ExcuseApplicationResponse> excuseApplicationResponse = createExcuseApplicationsResponse(excuseApplications, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("excuseApplications", excuseApplicationResponse);
		response.put("currentPage", pageExcuseApplications.getNumber());
		response.put("totalItems", pageExcuseApplications.getTotalElements());
		response.put("totalPages", pageExcuseApplications.getTotalPages());

		return response;
	}

}