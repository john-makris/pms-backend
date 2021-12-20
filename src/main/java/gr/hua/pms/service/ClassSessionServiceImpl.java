package gr.hua.pms.service;

import java.time.LocalDate;
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
import gr.hua.pms.exception.ResourceCannotBeDeletedException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.helper.DateTimeHelper;
import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ClassSession;
import gr.hua.pms.model.ERole;
import gr.hua.pms.model.Lecture;
import gr.hua.pms.payload.request.ClassSessionRequest;
import gr.hua.pms.payload.response.ClassSessionResponse;
import gr.hua.pms.repository.ClassGroupRepository;
import gr.hua.pms.repository.ClassSessionRepository;
import gr.hua.pms.repository.CourseScheduleRepository;
import gr.hua.pms.repository.GroupStudentRepository;
import gr.hua.pms.repository.LectureRepository;

@Service
public class ClassSessionServiceImpl implements ClassSessionService {
	
	@Autowired
	ClassSessionRepository classSessionRepository;
	
	@Autowired
	ClassGroupRepository classGroupRepository;
	
	@Autowired
	GroupStudentRepository groupStudentRepository;
	
	@Autowired
	CourseScheduleRepository courseScheduleRepository;
	
	@Autowired
	LectureRepository lectureRepository;
	
	@Autowired
	LectureService lectureService;
	
	@Autowired
	ClassGroupService classGroupService;
	
	@Autowired
	PresenceService presenceService;
	
	@Autowired
	UserService userService;

	@Override
	public Map<String, Object> findAllByLectureIdSortedPaginated(Long userId,
			Long lectureId, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ClassSession> classesSessions = new ArrayList<ClassSession>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ClassSession> pageClassesSessions = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageClassesSessions = classSessionRepository.searchByLectureIdSortedPaginated(lectureId, filter, pagingSort);
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_TEACHER)) {
				System.out.println("You are teacher");
				pageClassesSessions = classSessionRepository.searchByOwnerLectureIdSortedPaginated(lectureId, filter, pagingSort);
			}
		}

		pageClassesSessions = classSessionRepository.searchByLectureIdSortedPaginated(lectureId, filter, pagingSort);
		
		classesSessions = pageClassesSessions.getContent();

		if(classesSessions.isEmpty()) {
			return null;
		}
				
		List<ClassSessionResponse> classesSessionsResponse = createClassesSessionsResponse(classesSessions);

		Map<String, Object> response = new HashMap<>();
		response.put("classesSessions", classesSessionsResponse);
		response.put("currentPage", pageClassesSessions.getNumber());
		response.put("totalItems", pageClassesSessions.getTotalElements());
		response.put("totalPages", pageClassesSessions.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByLectureIdAndStatusSortedPaginated(Long userId, Long lectureId, String status, String filter,
			int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ClassSession> classesSessions = new ArrayList<ClassSession>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ClassSession> pageClassesSessions = null;
		
		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			pageClassesSessions = classSessionRepository.searchByLectureIdAndStatusSortedPaginated(lectureId,
					typeOfStatusModerator(status), filter, pagingSort);
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_TEACHER)) {
				System.out.println("You are teacher");
				pageClassesSessions = classSessionRepository.searchByOwnerLectureIdAndStatusSortedPaginated(lectureId,
						typeOfStatusModerator(status), filter, pagingSort);
			}
		}
		
		classesSessions = pageClassesSessions.getContent();

		if(classesSessions.isEmpty()) {
			return null;
		}
				
		List<ClassSessionResponse> classesSessionsResponse = createClassesSessionsResponse(classesSessions);

		Map<String, Object> response = new HashMap<>();
		response.put("classesSessions", classesSessionsResponse);
		response.put("currentPage", pageClassesSessions.getNumber());
		response.put("totalItems", pageClassesSessions.getTotalElements());
		response.put("totalPages", pageClassesSessions.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByUserIdAndStatusSortedPaginated(Long userId, Boolean status, String filter,
			int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ClassSession> classesSessions = new ArrayList<ClassSession>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ClassSession> pageClassesSessions = null;

		pageClassesSessions = classSessionRepository.searchByUserIdAndStatusSortedPaginated(userId, status, filter, pagingSort);
		
		classesSessions = pageClassesSessions.getContent();

		if(classesSessions.isEmpty()) {
			return null;
		}
				
		List<ClassSessionResponse> classesSessionsResponse = createClassesSessionsResponse(classesSessions);

		Map<String, Object> response = new HashMap<>();
		response.put("classesSessions", classesSessionsResponse);
		response.put("currentPage", pageClassesSessions.getNumber());
		response.put("totalItems", pageClassesSessions.getTotalElements());
		response.put("totalPages", pageClassesSessions.getTotalPages());

		return response;
	}
	
	@Override
	public ClassSessionResponse findClassSessionResponseById(Long id, Long userId) {
		ClassSession classSession = new ClassSession();

		if (userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are admin");
			classSession = classSessionRepository.findById(id).orElse(null);
		} else {
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_TEACHER)) {
				System.out.println("You are teacher");
				classSession = classSessionRepository.checkOwnershipByClassSessionId(id);
				if (classSession == null) {
					throw new BadRequestDataException("You don't have view privilege for this class session, since you are not the owner");
				}
			}
		}
		return createClassSessionResponse(classSession);
	}
	
	@Override
	public ClassSession findById(Long id) {
		ClassSession classSession = classSessionRepository.findById(id).orElse(null);
		return classSession;
	}
	
	@Override
	public ClassSessionResponse findClassSessionResponseByLectureIdAndStudentId(Long lectureId, Long studentId) {
		ClassSession classSession = classSessionRepository.searchByLectureIdAndStudentId(lectureId, studentId);
		return classSession != null ? createClassSessionResponse(classSession) : null;
	}
	
	@Override
	public ClassSessionResponse findPresentedClassSessionResponseByStudentIdAndStatus(Long studentId, Boolean status) {
		ClassSession classSession = classSessionRepository.searchCurrentClassSessionByStudentIdAndPresenceStatus(studentId, true, true);
		System.out.println("Class Session: "+classSession);
		return classSession != null ? createClassSessionResponse(classSession) : null;
	}
	
	@Override
	public ClassSession save(ClassSessionRequest classSessionRequestData, Long userId) {
		
		if (!userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are not admin");
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_TEACHER)) {
				if ((courseScheduleRepository.checkOwnershipByCourseScheduleId(classSessionRequestData.getLecture().getCourseSchedule().getId())) == null
						|| (lectureRepository.checkOwnershipByLectureId(classSessionRequestData.getLecture().getId())) == null
						|| (classGroupRepository.checkOwnerShipByClassGroupId(classSessionRequestData.getClassGroup().getId())) == null) {
					throw new BadRequestDataException("You cannot create this class session, because you are not the owner of the schedule,"
							+ " lecture or group");
				}
			}
		}
		
		ClassSession _classSession = new ClassSession();
		ClassGroup classGroup = classSessionRequestData.getClassGroup();
		Lecture lecture = classSessionRequestData.getLecture();
		
		LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
				classSessionRequestData.getClassGroup().getStartTime());
		
		LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
				classSessionRequestData.getClassGroup().getEndTime());
		
		/*
		ClassSession preExistingClassSession = classSessionRepository.checkClassSessionValidity(
				startDateTime, endDateTime, classGroup.getRoom().getRoomIdentifier());
		
		if (preExistingClassSession != null) {
			throw new BadRequestDataException("Inside this date & time and room "
					+classGroup.getRoom().getRoomIdentifier()
					+", there is already "+preExistingClassSession.getNameIdentifier()+" for "+lecture.getNameIdentifier()+" of "
					+lecture.getCourseSchedule().getCourse().getName()+" schedule "+": Please select another date or group");
		} */
		
		if (createCurrentTimestamp().isAfter(startDateTime)) {
			throw new BadRequestDataException("You cannot create a class session using a past date and time");
		}
		
		if (!(classSessionRepository.searchByLectureIdClassGroupId(lecture.getId(), classGroup.getId())).isEmpty()) {
			throw new BadRequestDataException("A class session for "+lecture.getCourseSchedule().getCourse().getName()+" Schedule's "
					+lecture.getNameIdentifier()+" and "+classGroup.getNameIdentifier()+" already exists");
		}
		
		String nameIdentifier = createSimpleNameIdentifier(classSessionRequestData.getIdentifierSuffix());
		
		_classSession.setStartDateTime(startDateTime);
		_classSession.setEndDateTime(endDateTime);
		
		startDateTimeValidator(_classSession.getStartDateTime(), lecture);

		_classSession.setClassGroup(classGroup);
		_classSession.setLecture(lecture);
		
		_classSession.setStudents(groupStudentRepository.searchStudentsOfGroup(classGroup.getId()));
		
		_classSession.setPresenceStatementStatus(classSessionRequestData.getPresenceStatementStatus());
		_classSession.setStatus(null);
		
		System.out.println("Start Time: "+startDateTime);
		
		if (!classSessionRepository.searchByLectureIdAndNameIdentifier(lecture.getId(), nameIdentifier).isEmpty()) {
			throw new BadRequestDataException(nameIdentifier+" for "
					+classGroup.getCourseSchedule().getCourse().getName()+" "
					+lecture.getNameIdentifier()+", already exists");
		}
		
		if (!classGroupService.checkClassGroupCompleteness(classGroup)) {
			throw new BadRequestDataException("The choosen class group has not the required minimum number of "
					+classGroupService.classGroupCompletenessValidator(classGroup.getCapacity())+" students");
		}
		
		_classSession.setNameIdentifier(nameIdentifier);

		ClassSession classSession = classSessionRepository.save(_classSession);
		
		return classSession;
	}
	
	@Override
	public ClassSession update(Long id, Long userId, ClassSessionRequest classSessionRequestData) {
		ClassSession _classSession = classSessionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Class Session with id = " + id));
		
		if (!userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
			System.out.println("You are not admin");
			if (classSessionRepository.checkOwnershipByClassSessionId(id) == null) {
				throw new BadRequestDataException("You cannot update this class session, because you are not its owner");
			}
			
			if (userService.takeAuthorities(userId).contains(ERole.ROLE_TEACHER)) {
				if ((courseScheduleRepository.checkOwnershipByCourseScheduleId(classSessionRequestData.getLecture().getCourseSchedule().getId())) == null
						|| (lectureRepository.checkOwnershipByLectureId(classSessionRequestData.getLecture().getId())) == null
						|| (classGroupRepository.checkOwnerShipByClassGroupId(classSessionRequestData.getClassGroup().getId())) == null) {
					throw new BadRequestDataException("You cannot update this class session, because you are not the owner of the schedule,"
							+ " lecture or group");
				}
			}
		}
		
		Lecture lecture = classSessionRequestData.getLecture();
		System.out.println("Lecture: "+lecture.getNameIdentifier());
		String nameIdentifier = createSimpleNameIdentifier(classSessionRequestData.getIdentifierSuffix());
		ClassGroup classGroup = classSessionRequestData.getClassGroup();
		LocalDateTime currentTimeStamp = createCurrentTimestamp();
		
		if (_classSession.getStatus() == null) {
			if (currentTimeStamp.isAfter(_classSession.getEndDateTime()) || 
					(currentTimeStamp.isAfter(_classSession.getStartDateTime()) && 
					currentTimeStamp.isBefore(_classSession.getEndDateTime()))) {
				throw new BadRequestDataException("You can only update a pending class session");
			}
			
			if (currentTimeStamp.isAfter(_classSession.getStartDateTime()) && 
					currentTimeStamp.isBefore(_classSession.getEndDateTime())) {
				throw new BadRequestDataException("You cannot change the date of a current session");
			} else if (currentTimeStamp.isAfter(_classSession.getEndDateTime())) {
				throw new BadRequestDataException("You cannot change the date of a past session");
			} else {
				if (currentTimeStamp.isAfter(LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
						classSessionRequestData.getClassGroup().getStartTime()))) {
					throw new BadRequestDataException("You cannot update a class session using a past date and time");
				}
				
				_classSession.setStartDateTime(
						LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
								classSessionRequestData.getClassGroup().getStartTime()));
				_classSession.setEndDateTime(
						LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
								classSessionRequestData.getClassGroup().getEndTime()));
				startDateTimeValidator(_classSession.getStartDateTime(), lecture);
			}
			
			if (!classSessionRepository.searchByLectureIdAndNameIdentifier(lecture.getId(), nameIdentifier).isEmpty()
					&& !_classSession.getNameIdentifier().equals(nameIdentifier)) {
				throw new BadRequestDataException(nameIdentifier+" for "+classGroup.getNameIdentifier()+" of "
						+classGroup.getCourseSchedule().getCourse().getName()+" "
						+lecture.getNameIdentifier()+", already exists");		
			}
			
			_classSession.setNameIdentifier(nameIdentifier);
			_classSession.setLecture(lecture);
			_classSession.setClassGroup(classGroup);
			
			_classSession.setStudents(groupStudentRepository.searchStudentsOfGroup(classGroup.getId()));
			
		} else if(_classSession.getStatus()) {
			if (currentTimeStamp.isAfter(_classSession.getEndDateTime())) {
				throw new BadRequestDataException("You can only update a pending class session");
			}
			_classSession.setPresenceStatementStatus(classSessionRequestData.getPresenceStatementStatus());
		} else {
			throw new BadRequestDataException("You cannot update a past class session !");
		}
		
		return classSessionRepository.save(_classSession);
	}
	
	private ClassSession updateClassSessionStatus(Long id) {
		ClassSession _classSession = classSessionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Class Session with id = " + id));
		
		LocalDateTime currentTimestamp = createCurrentTimestamp();
		
		if (currentTimestamp.isAfter(_classSession.getStartDateTime()) && 
				currentTimestamp.isBefore(_classSession.getEndDateTime())) {
			_classSession.setStatus(true);
		} else if (currentTimestamp.isAfter(_classSession.getEndDateTime())) {
			_classSession.setStatus(false);
			if (_classSession.getPresenceStatementStatus()) {
				_classSession.setPresenceStatementStatus(false);
				presenceService.updatePresences(id);			
			}
		} else {
			_classSession.setStatus(null);
		}
		
		return classSessionRepository.save(_classSession);
	}
	
	private String createSimpleNameIdentifier(String identifierSuffix) {
		String identifier = "session_"+identifierSuffix;

		return identifier;
	}
	
	@Override
	public void deleteById(Long id, Long userId) {
		ClassSession classSession = classSessionRepository.findById(id).orElse(null);
		
		if(classSession!=null) {
			
			if (!userService.takeAuthorities(userId).contains(ERole.ROLE_ADMIN)) {
				System.out.println("You are not admin");
				if (classSessionRepository.checkOwnershipByClassSessionId(classSession.getId()) == null) {
					throw new BadRequestDataException("You cannot delete the session, since you are not the owner");
				}
			}
			
			if (classSession.getStatus() != null) {
				throw new ResourceCannotBeDeletedException("You can only delete a pending class session");
			}
			
		} else {
			throw new IllegalArgumentException();
		}
		
		classSessionRepository.deleteById(id);
	}
	
	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);
	    
	    if (sort[0].matches("course")) {
	    	sort[0] = "lecture.courseSchedule.course.name";
	    }
	    
	    if (sort[0].matches("lecture")) {
	    	sort[0] = "lecture.nameIdentifier";
	    }
	    
	    if (sort[0].matches("dateTime")) {
	    	sort[0] = "startDateTime";
	    }
	    
	    if (sort[0].matches("date")) {
	    	sort[0] = "startDateTime";
	    }
	    
	    if (sort[0].matches("classGroup")) {
	    	sort[0] = "classGroup.nameIdentifier";
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

	    if (typeOfStatus.matches("Current")) {
		    System.out.println("SPOT E");
	    	return true;
	    } else if (typeOfStatus.matches("Past")) {
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
	
	private void startDateTimeValidator(LocalDateTime startDateTime, Lecture lecture) {
		boolean isWinterCourseSeason = false;
		boolean isMonthValid = monthValidator(startDateTime);
		
		if (lecture.getCourseSchedule().getCourse().getSemester().getSemesterNumber()%2!=0) {
			isWinterCourseSeason = true;
		} else {
			isWinterCourseSeason = false;
		}
		
		boolean isWinterDateTimeSeason = DateTimeHelper.calcDateTimeSeason(startDateTime);
		
		
		
		if ((isWinterCourseSeason != isWinterDateTimeSeason) || isMonthValid == false) {
			throw new BadRequestDataException(isWinterCourseSeason == true ?
					"This is a winter semester course: Put month value between October and February" :
						"This is a spring semester course: Put month value between Martch and June");
		}
	}
	
	private boolean monthValidator(LocalDateTime startDateTime) {
		int monthValue = startDateTime.getMonth().getValue();
		if (monthValue >= 7 && monthValue <= 9) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public List<ClassSessionResponse> createClassesSessionsResponse(List<ClassSession> classesSessions) {
		List<ClassSessionResponse> classesSessionsResponse = new ArrayList<ClassSessionResponse>();
		
		classesSessions.forEach(classSession -> {
			ClassSession _classSession = updateClassSessionStatus(classSession.getId());
			ClassSessionResponse classSessionResponse = 
					new ClassSessionResponse(
							_classSession.getId(),
							_classSession.getNameIdentifier().split("_", _classSession.getNameIdentifier().length())[1],
							_classSession.getNameIdentifier(),
							LocalDate.of(_classSession.getStartDateTime().getYear(), 
									_classSession.getStartDateTime().getMonth(), 
									_classSession.getStartDateTime().getDayOfMonth()),
							_classSession.getPresenceStatementStatus(),
							_classSession.getStatus(),
							lectureService.createLectureResponse(_classSession.getLecture()),
							classGroupService.createClassGroupResponse(_classSession.getClassGroup()));
			classesSessionsResponse.add(classSessionResponse);
		});
		
		return classesSessionsResponse;
	}
	
	@Override
	public ClassSessionResponse createClassSessionResponse(ClassSession classSession) {
		ClassSession _classSession = updateClassSessionStatus(classSession.getId());
		return new ClassSessionResponse(
				_classSession.getId(),
				_classSession.getNameIdentifier().split("_", classSession.getNameIdentifier().length())[1],
				_classSession.getNameIdentifier(),
				LocalDate.of(_classSession.getStartDateTime().getYear(), 
						_classSession.getStartDateTime().getMonth(), 
						_classSession.getStartDateTime().getDayOfMonth()),
				_classSession.getPresenceStatementStatus(),
				_classSession.getStatus(),
				lectureService.createLectureResponse(_classSession.getLecture()),
				classGroupService.createClassGroupResponse(_classSession.getClassGroup()));
	}

}