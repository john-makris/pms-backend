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
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ClassSession;
import gr.hua.pms.model.Lecture;
import gr.hua.pms.payload.request.ClassSessionRequest;
import gr.hua.pms.payload.response.ClassSessionResponse;
import gr.hua.pms.repository.ClassSessionRepository;
import gr.hua.pms.repository.GroupStudentRepository;

@Service
public class ClassSessionServiceImpl implements ClassSessionService {
	
	@Autowired
	ClassSessionRepository classSessionRepository;
	
	@Autowired
	GroupStudentRepository groupStudentRepository;
	
	@Autowired
	LectureService lectureService;
	
	@Autowired
	ClassGroupService classGroupService;
	
	@Autowired
	PresenceService presenceService;

	@Override
	public Map<String, Object> findAllByLectureIdSortedPaginated(Long lectureId,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ClassSession> classesSessions = new ArrayList<ClassSession>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ClassSession> pageClassesSessions = null;

		pageClassesSessions = classSessionRepository.searchByLectureIdAndClassGroupIdSortedPaginated(lectureId, filter, pagingSort);
		
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
	public ClassSessionResponse findClassSessionResponseById(Long id) {
		ClassSession classSession = classSessionRepository.findById(id).orElse(null);
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
	public ClassSession save(ClassSessionRequest classSessionRequestData) {
		ClassSession _classSession = new ClassSession();
		ClassGroup classGroup = classSessionRequestData.getClassGroup();
		Lecture lecture = classSessionRequestData.getLecture();
		
		if (!(classSessionRepository.searchByLectureIdClassGroupId(lecture.getId(), classGroup.getId())).isEmpty()) {
			throw new BadRequestDataException("A class session for "+lecture.getCourseSchedule().getCourse().getName()+" Schedule's "
					+lecture.getNameIdentifier()+" and "+classGroup.getNameIdentifier()+" already exists");
		}
		
		String nameIdentifier = createSimpleNameIdentifier(classSessionRequestData.getIdentifierSuffix());
		
		_classSession.setStartDateTime(
				LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
						classSessionRequestData.getClassGroup().getStartTime()));
		_classSession.setEndDateTime(
				LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
						classSessionRequestData.getClassGroup().getEndTime()));

		_classSession.setClassGroup(classGroup);
		_classSession.setLecture(lecture);
		
		_classSession.setStudents(groupStudentRepository.searchStudentsOfGroup(classGroup.getId()));
		
		_classSession.setPresenceStatementStatus(classSessionRequestData.getPresenceStatementStatus());
		_classSession.setStatus(null);
		
		System.out.println("Start Time: "+LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
						classSessionRequestData.getClassGroup().getStartTime()));
		
		if (!classSessionRepository.searchByLectureIdAndNameIdentifier(lecture.getId(), nameIdentifier).isEmpty()) {
			throw new BadRequestDataException(nameIdentifier+" for "
					+classGroup.getCourseSchedule().getCourse().getName()+" "
					+lecture.getNameIdentifier()+", already exists");
		}
		
		_classSession.setNameIdentifier(nameIdentifier);

		ClassSession classSession = classSessionRepository.save(_classSession);
		
		return classSession;
	}
	
	@Override
	public ClassSession update(Long id, ClassSessionRequest classSessionRequestData) {
		ClassSession _classSession = classSessionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Class Session with id = " + id));
		
		Lecture lecture = classSessionRequestData.getLecture();
		System.out.println("Lecture: "+lecture.getNameIdentifier());
		String nameIdentifier = createSimpleNameIdentifier(classSessionRequestData.getIdentifierSuffix());
		ClassGroup classGroup = classSessionRequestData.getClassGroup();

		_classSession.setLecture(lecture);
		_classSession.setClassGroup(classGroup);
		
		_classSession.setStudents(groupStudentRepository.searchStudentsOfGroup(classGroup.getId()));
		
		_classSession.setStartDateTime(
				LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
						classSessionRequestData.getClassGroup().getStartTime()));
		_classSession.setEndDateTime(
				LocalDateTime.of(LocalDate.parse(classSessionRequestData.getDate()),
						classSessionRequestData.getClassGroup().getEndTime()));
		
		_classSession.setPresenceStatementStatus(classSessionRequestData.getPresenceStatementStatus());
		//_classSession.setStatus(false);
		
		if (!classSessionRepository.searchByLectureIdAndNameIdentifier(lecture.getId(), nameIdentifier).isEmpty()
				&& !_classSession.getNameIdentifier().equals(nameIdentifier)) {
			throw new BadRequestDataException(nameIdentifier+" for "+classGroup.getNameIdentifier()+" of "
					+classGroup.getCourseSchedule().getCourse().getName()+" "
					+lecture.getNameIdentifier()+", already exists");		
		}
		
		_classSession.setNameIdentifier(nameIdentifier);
		
		return classSessionRepository.save(_classSession);
	}
	
	private String createSimpleNameIdentifier(String identifierSuffix) {
		String identifier = "session_"+identifierSuffix;
		/*
		if (name.equals(ELectureType.Theory)) {
			identifier = "theory_"+identifierSuffix;
		}
		
		if (name.equals(ELectureType.Lab)) {
			identifier = "lab_"+identifierSuffix;
		}
		*/
		return identifier;
	}
	
	@Override
	public void deleteById(Long id) {
		ClassSession classSession = classSessionRepository.findById(id).orElse(null);
		
		if(classSession!=null) {
			/*if (groupStudentRepository.existsByClassGroupId(classSession.getId())) {
				throw new ResourceCannotBeDeletedException("You cannot delete "+
			classSession.getGroupType().getName().toString().toLowerCase()+"_"
						+classSession.getNameIdentifier()+" of "+classSession.getCourseSchedule().getCourse().getName()+" schedule"+
						", "+"since it has student subscriptions");
			} else {*/
				classSessionRepository.deleteById(id);
			/*}*/
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);
	    
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
	
	@Override
	public List<ClassSessionResponse> createClassesSessionsResponse(List<ClassSession> classesSessions) {
		List<ClassSessionResponse> classesSessionsResponse = new ArrayList<ClassSessionResponse>();
		
		classesSessions.forEach(classSession -> {
			ClassSessionResponse classSessionResponse = 
					new ClassSessionResponse(
							classSession.getId(),
							classSession.getNameIdentifier().split("_", classSession.getNameIdentifier().length())[1],
							classSession.getNameIdentifier(),
							LocalDate.of(classSession.getStartDateTime().getYear(), 
									classSession.getStartDateTime().getMonth(), 
									classSession.getStartDateTime().getDayOfMonth()),
							classSession.getPresenceStatementStatus(),
							classSession.getStatus(),
							lectureService.createLectureResponse(classSession.getLecture()),
							classGroupService.createClassGroupResponse(classSession.getClassGroup()));
			classesSessionsResponse.add(classSessionResponse);
		});
		
		return classesSessionsResponse;
	}
	
	@Override
	public ClassSessionResponse createClassSessionResponse(ClassSession classSession) {
		return new ClassSessionResponse(
				classSession.getId(),
				classSession.getNameIdentifier().split("_", classSession.getNameIdentifier().length())[1],
				classSession.getNameIdentifier(),
				LocalDate.of(classSession.getStartDateTime().getYear(), 
						classSession.getStartDateTime().getMonth(), 
						classSession.getStartDateTime().getDayOfMonth()),
				classSession.getPresenceStatementStatus(),
				classSession.getStatus(),
				lectureService.createLectureResponse(classSession.getLecture()),
				classGroupService.createClassGroupResponse(classSession.getClassGroup()));
	}

}