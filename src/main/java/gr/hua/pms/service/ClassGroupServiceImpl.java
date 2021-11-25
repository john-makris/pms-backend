package gr.hua.pms.service;

import java.time.LocalTime;
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
import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.LectureType;
import gr.hua.pms.payload.request.ClassGroupRequest;
import gr.hua.pms.payload.response.ClassGroupResponse;
import gr.hua.pms.repository.ClassGroupRepository;
import gr.hua.pms.repository.GroupStudentRepository;

@Service
public class ClassGroupServiceImpl implements ClassGroupService {

	@Autowired
	ClassGroupRepository classGroupRepository;
	
	@Autowired
	GroupStudentRepository groupStudentRepository;
	
	@Autowired
	CourseScheduleService courseScheduleService;
	
	@Override
	public Map<String, Object> findAllByDepartmentAndCourseScheduleIdSortedPaginated(Long departmentId,
			Long courseScheduleId, String filter, int page, int size, String[] sort) {

		List<Order> orders = createOrders(sort);

		List<ClassGroup> classesGroups = new ArrayList<ClassGroup>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ClassGroup> pageClassesGroups = null;

		pageClassesGroups = classGroupRepository.searchByCourseSchedulePerDepartmentAndFilterSortedPaginated(departmentId, courseScheduleId, filter, pagingSort);
		
		classesGroups = pageClassesGroups.getContent();

		if(classesGroups.isEmpty()) {
			return null;
		}
		
		List<ClassGroupResponse> classesGroupsResponse = createClassesGroupsResponse(classesGroups);
				
		Map<String, Object> response = new HashMap<>();
		response.put("classesGroups", classesGroupsResponse);
		response.put("currentPage", pageClassesGroups.getNumber());
		response.put("totalItems", pageClassesGroups.getTotalElements());
		response.put("totalPages", pageClassesGroups.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByCourseScheduleIdPerTypeSortedPaginated(
			Long courseScheduleId, ELectureType name, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<ClassGroup> classesGroups = new ArrayList<ClassGroup>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ClassGroup> pageClassesGroups = null;

		pageClassesGroups = classGroupRepository.searchByCourseSchedulePerTypeWithFilterSortedPaginated(courseScheduleId, name, filter, pagingSort);
		
		classesGroups = pageClassesGroups.getContent();

		if(classesGroups.isEmpty()) {
			return null;
		}
				
		List<ClassGroupResponse> classesGroupsResponse = createClassesGroupsResponse(classesGroups);

		Map<String, Object> response = new HashMap<>();
		response.put("classesGroups", classesGroupsResponse);
		response.put("currentPage", pageClassesGroups.getNumber());
		response.put("totalItems", pageClassesGroups.getTotalElements());
		response.put("totalPages", pageClassesGroups.getTotalPages());

		return response;
	}

	@Override
	public List<ClassGroup> findAll(String[] sort) {
		try {
			return classGroupRepository.findAll(Sort.by(createOrders(sort)));
		}catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public ClassGroupResponse findById(Long id) {
		ClassGroup classGroup = classGroupRepository.findById(id).orElse(null);
		return createClassGroupResponse(classGroup);
	}

	@Override
	public ClassGroup save(ClassGroupRequest classGroupRequestData) {
		ClassGroup _classGroup = new ClassGroup();
		CourseSchedule courseSchedule = classGroupRequestData.getCourseSchedule();
		LectureType lectureType = classGroupRequestData.getGroupType();
		String nameIdentifier = createSimpleNameIdentifier(lectureType.getName(), classGroupRequestData.getIdentifierSuffix());
		
		_classGroup.setCourseSchedule(courseSchedule);
		_classGroup.setGroupType(lectureType);
		
		_classGroup.setCapacity(classGroupRequestData.getCapacity());
		
		_classGroup.setStartTime(LocalTime.parse(classGroupRequestData.getStartTime()));		
		_classGroup.setEndTime(LocalTime.parse(classGroupEndTimeModerator(classGroupRequestData)));
		_classGroup.setRoom(classGroupRequestData.getRoom());
		_classGroup.setStatus(classGroupRequestData.getStatus());
		
		if (!classGroupRepository.searchByCourseScheduleIdAndLectureTypeNameAndNameIdentifier(courseSchedule.getId(), lectureType.getName(), nameIdentifier).isEmpty()) {
			throw new BadRequestDataException(nameIdentifier+" for "+courseSchedule.getCourse().getName()+" "+lectureType.getName().toString().toLowerCase()+" groups"+", already exists");
		}
		
		_classGroup.setNameIdentifier(nameIdentifier);
		
		ClassGroup classGroup = classGroupRepository.save(_classGroup);
		
		return classGroup;
	}
	

	private String createSimpleNameIdentifier(ELectureType name, String identifierSuffix) {
		String identifier = "group_"+identifierSuffix;
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
	public ClassGroup update(Long id, ClassGroupRequest classGroupRequestData) {
		ClassGroup _classGroup = classGroupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Class Group with id = " + id));
		
		LectureType groupType = classGroupRequestData.getGroupType();
		System.out.println("Group Type: "+groupType.getName());
		String nameIdentifier = createSimpleNameIdentifier(groupType.getName(), classGroupRequestData.getIdentifierSuffix());
		CourseSchedule courseSchedule = classGroupRequestData.getCourseSchedule();

		_classGroup.setCourseSchedule(courseSchedule);
		_classGroup.setGroupType(groupType);
		
		int groupsOfStudentsNumber = groupStudentRepository.searchByClassGroupId(_classGroup.getId()).size();

		if (isNewCapacityAppropriate(groupsOfStudentsNumber, _classGroup.getCapacity())) {
			_classGroup.setCapacity(classGroupRequestData.getCapacity());

		} else {
			throw new BadRequestDataException("You cannot set group capacity to "+classGroupRequestData.getCapacity()+" since you have "
					+groupsOfStudentsNumber+" students subscribed");
		}
		
		_classGroup.setStartTime(LocalTime.parse(classGroupRequestData.getStartTime()));		
		_classGroup.setEndTime(LocalTime.parse(classGroupEndTimeModerator(classGroupRequestData)));
		_classGroup.setRoom(classGroupRequestData.getRoom());
		_classGroup.setStatus(classGroupRequestData.getStatus());
		
		if (!classGroupRepository.searchByCourseScheduleIdAndLectureTypeNameAndNameIdentifier(courseSchedule.getId(), groupType.getName(), nameIdentifier).isEmpty() && !_classGroup.getNameIdentifier().equals(nameIdentifier)) {
			throw new BadRequestDataException(nameIdentifier+" for "+courseSchedule.getCourse().getName()+" "+groupType.getName().toString().toLowerCase()+" groups"+", already exists");
		}
		_classGroup.setNameIdentifier(nameIdentifier);
		
		return classGroupRepository.save(_classGroup);
	}
	
	private boolean isNewCapacityAppropriate(int groupsOfStudentsNumber, int capacity) {
		
		if (capacity >= groupsOfStudentsNumber) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void deleteById(Long id) {
		ClassGroup classGroup = classGroupRepository.findById(id).orElse(null);
		
		if(classGroup!=null) {
			if (groupStudentRepository.existsByClassGroupId(classGroup.getId())) {
				throw new ResourceCannotBeDeletedException("You cannot delete "+
			classGroup.getGroupType().getName().toString().toLowerCase()+"_"
						+classGroup.getNameIdentifier()+" of "+classGroup.getCourseSchedule().getCourse().getName()+" schedule"+
						", "+"since it has student subscriptions");
			} else {
				classGroupRepository.deleteById(id);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		classGroupRepository.deleteAll();
	}
	
	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);

	    if (sort[0].matches("name")) {
	    	sort[0] = "nameIdentifier";
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
	public List<ClassGroupResponse> createClassesGroupsResponse(List<ClassGroup> classesGroups) {
		List<ClassGroupResponse> classesGroupsResponse = new ArrayList<ClassGroupResponse>();
		
		classesGroups.forEach(classGroup -> {
			ClassGroupResponse classGroupResponse = 
					new ClassGroupResponse(
							classGroup.getId(),
							classGroup.getNameIdentifier().split("_", classGroup.getNameIdentifier().length())[1],
							classGroup.getNameIdentifier(),
							classGroup.getStartTime().toString(),
							classGroup.getEndTime().toString(),
							classGroup.getCapacity(),
							groupStudentRepository.searchByClassGroupId(classGroup.getId()).size(),
							classGroup.getGroupType(),
							classGroup.getStatus(),
							courseScheduleService.createCourseScheduleResponse(classGroup.getCourseSchedule()),
							classGroup.getRoom());
			classesGroupsResponse.add(classGroupResponse);
		});
		
		return classesGroupsResponse;
	}
	
	@Override
	public ClassGroupResponse createClassGroupResponse(ClassGroup classGroup) {
		return new ClassGroupResponse(
				classGroup.getId(),
				classGroup.getNameIdentifier().split("_", classGroup.getNameIdentifier().length())[1],
				classGroup.getNameIdentifier(),
				classGroup.getStartTime().toString(),
				classGroup.getEndTime().toString(),
				classGroup.getCapacity(),
				groupStudentRepository.searchByClassGroupId(classGroup.getId()).size(),
				classGroup.getGroupType(),
				classGroup.getStatus(),
				courseScheduleService.createCourseScheduleResponse(classGroup.getCourseSchedule()),
				classGroup.getRoom());
	}
	
	private String classGroupEndTimeModerator(ClassGroupRequest classGroupRequestData) {
		CourseSchedule courseSchedule = classGroupRequestData.getCourseSchedule();
		LectureType lectureType = classGroupRequestData.getGroupType();
		LocalTime startTime = LocalTime.parse(classGroupRequestData.getStartTime());
		LocalTime endTime = startTime;
		int duration = 0;

		if (lectureType.getName().equals(ELectureType.Theory)) {
			duration = courseSchedule.getTheoryLectureDuration();
			return String.valueOf(classGroupEndTimeCalculator(endTime, duration));
		} else if (lectureType.getName().equals(ELectureType.Lab)) {
			duration = courseSchedule.getLabLectureDuration();
			return String.valueOf(classGroupEndTimeCalculator(endTime, duration));
		} else {
			throw new BadRequestDataException("Lecture type cannot recognized");
		}		
				
	}
	
	private LocalTime classGroupEndTimeCalculator(LocalTime endTime, int duration) {
		long hours = 0;
		long minutes = 0;
		hours = duration / 3600;
		System.out.println("Hours: "+hours);
		minutes = (duration % 3600) / 60;
		System.out.println("Minutes: "+minutes);
		
		System.out.println("End Time: "+endTime);

		endTime = endTime.plusHours(hours);
		System.out.println("End Time plus hours: "+endTime);
		endTime = endTime.plusMinutes(minutes);
		System.out.println("End Time plus hours & minutes: "+endTime);
		return endTime;
	}

}
