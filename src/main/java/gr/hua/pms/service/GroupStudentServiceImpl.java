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

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.GroupStudent;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.GroupStudentRequestData;
import gr.hua.pms.payload.response.UserResponse;
import gr.hua.pms.repository.ClassGroupRepository;
import gr.hua.pms.repository.ClassSessionRepository;
import gr.hua.pms.repository.GroupStudentRepository;
import gr.hua.pms.repository.RoleRepository;
import gr.hua.pms.repository.UserRepository;

@Service
public class GroupStudentServiceImpl implements GroupStudentService {

	@Autowired
	GroupStudentRepository groupStudentRepository;
	
	@Autowired
	ClassGroupRepository classesGroupsRepository;
	
	@Autowired
	ClassSessionRepository classesSessionsRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UserService userService;
	
	@Override
	public Map<String, Object> findStudentsOfGroup(Long classGroupId, 
			String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);

		List<UserResponse> studentsOfGroup = new ArrayList<UserResponse>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageStudentsOfGroup = null;

		pageStudentsOfGroup = groupStudentRepository.searchStudentsOfGroupWithFilterSortedPaginated(classGroupId, filter, pagingSort);
		
		studentsOfGroup = userService.createUsersResponse(pageStudentsOfGroup.getContent());
		
		if(studentsOfGroup.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();

		response.put("studentsOfGroup", studentsOfGroup);
		response.put("currentPage", pageStudentsOfGroup.getNumber());
		response.put("totalItems", pageStudentsOfGroup.getTotalElements());
		response.put("totalPages", pageStudentsOfGroup.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentCourseSchedulePerTypeAndClassGroupSortedPaginated(Long departmentId,
			Long courseScheduleId, Long classGroupId, ELectureType name, String filter, int page, int size,
			String[] sort) {

		List<Order> orders = createOrders(sort);

		List<GroupStudent> groupsStudents = new ArrayList<GroupStudent>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<GroupStudent> pageGroupsStudents = null;

		pageGroupsStudents = groupStudentRepository.searchByDepartmentCourseSchedulePerTypeAndClassGroupWithFilterSortedPaginated(
				departmentId, courseScheduleId, classGroupId, name, filter, pagingSort);
		
		groupsStudents = pageGroupsStudents.getContent();

		if(groupsStudents.isEmpty()) {
			return null;
		}
		
		//List<GroupStudentResponse> groupsStudentsResponse = createGroupsStudentsResponse(groupsStudents);
				
		Map<String, Object> response = new HashMap<>();
		//response.put("groupsStudents", groupsStudentsResponse);
		response.put("groupsStudents", groupsStudents);
		response.put("currentPage", pageGroupsStudents.getNumber());
		response.put("totalItems", pageGroupsStudents.getTotalElements());
		response.put("totalPages", pageGroupsStudents.getTotalPages());

		return response;
	}

	@Override
	public List<GroupStudent> findAll(String[] sort) {
		try {
			return groupStudentRepository.findAll(Sort.by(createOrders(sort)));
		}catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GroupStudent findById(Long id) {
		GroupStudent groupStudent = groupStudentRepository.findById(id).orElse(null);
		//return createGroupsStudentsResponse(groupStudent);
		return groupStudent;
	}
	
	@Override
	public UserResponse findStudentOfGroup(Long studentId, Long classGroupId) {
		UserResponse student = userService.createUserResponse(groupStudentRepository.searchStudentOfGroup(studentId, classGroupId));
		return student;
	}

	@Override
	public ClassGroup findClassGroupByStudentIdAndCourseScheduleIdAndGroupType(Long studentId, Long courseScheduleId, ELectureType groupType) {
		ClassGroup classGroup = groupStudentRepository.searchClassGroupByStudentIdAndCourseScheduleIdAndGroupType(groupType, courseScheduleId, studentId);
		return classGroup;
	}
	
	@Override
	public GroupStudent save(GroupStudentRequestData groupStudentRequestData) {
		// it needs ownership
		if (!(classesSessionsRepository.searchByClassGroupId(groupStudentRequestData.getClassGroup().getId())).isEmpty()) {
			throw new BadRequestDataException("You cannot subscribe a student to a group that already exists on a session");
		}
		/*
		if (groupStudentRequestData.getClassGroup().getStatus() == false) {
			throw new BadRequestDataException("You cannot subscribe to group, since it is closed");
		}*/
		
		if (groupStudentRequestData.getClassGroup().getCapacity() == groupStudentRepository
				.searchStudentsOfGroup(groupStudentRequestData.getClassGroup().getId()).size()) {
			throw new BadRequestDataException("You cannot subscribe a student to group, since it is full of students");
		}
		
		if ( groupStudentRepository.existsByStudentIdAndClassGroupId(groupStudentRequestData.getStudentId(), 
				groupStudentRequestData.getClassGroup().getId())) {
			throw new BadRequestDataException("Student is already subscribed to "+groupStudentRequestData.getClassGroup().getNameIdentifier());
		}
		
		GroupStudent _groupStudent = new GroupStudent();
		User student = userRepository.findById(groupStudentRequestData.getStudentId()).orElse(null);
		if (student != null) { // check if it is a student
			if (groupStudentRepository.searchByStudentIdAndCourseScheduleIdAndGroupType(
					groupStudentRequestData.getClassGroup().getGroupType().getName(),
					groupStudentRequestData.getClassGroup().getCourseSchedule().getId(),
					groupStudentRequestData.getStudentId()).isEmpty()) {
				_groupStudent.setStudent(student);
				_groupStudent.setClassGroup(groupStudentRequestData.getClassGroup());
				return checkClassGroupCapacity(_groupStudent);
			} else {
				throw new BadRequestDataException("Student is already subscribed in a "+
			groupStudentRequestData.getClassGroup().getGroupType().getName().toString().toLowerCase()+" group for "
					+ groupStudentRequestData.getClassGroup().getCourseSchedule().getCourse().getName()+" schedule");
			}
		} else {
			throw new BadRequestDataException("User with id "+groupStudentRequestData.getStudentId()+", is not a student");
		}
	}
	
	private GroupStudent checkClassGroupCapacity(GroupStudent groupStudent) {
		int groupsOfStudentsNumber = groupStudentRepository.searchByClassGroupId(groupStudent.getClassGroup().getId()).size();
		System.out.println("groupsOfStudentsNumber: "+groupsOfStudentsNumber);
		System.out.println("Capacity: "+groupStudent.getClassGroup());
		System.out.println("Group Type: "+groupStudent.getClassGroup().getGroupType().getName());
		if (groupStudent.getClassGroup().getGroupType().getName().equals(ELectureType.Theory)) {
			if (groupStudent.getClassGroup().getCapacity() > groupsOfStudentsNumber) {
				System.out.println("Capacity: "+groupStudent.getClassGroup().getCapacity());
				return groupStudentRepository.save(groupStudent);
			} else {
				throw new BadRequestDataException("theory_"+groupStudent.getClassGroup().getNameIdentifier()+" is full");
			}
		} else if (groupStudent.getClassGroup().getGroupType().getName().equals(ELectureType.Lab)) {
			if (groupStudent.getClassGroup().getCapacity() > groupsOfStudentsNumber) {
				return groupStudentRepository.save(groupStudent);
			} else {
				throw new BadRequestDataException("lab_"+groupStudent.getClassGroup().getNameIdentifier()+" is full");
			}
		} else {
			throw new BadRequestDataException("Group type could not recognized");
		}

	}

	@Override
	public GroupStudent update(Long id, GroupStudent groupStudent) {
		GroupStudent _groupStudent = groupStudentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Group Student with id = " + id));
		
		_groupStudent.setStudent(groupStudent.getStudent());
		_groupStudent.setClassGroup(groupStudent.getClassGroup());
		
		return groupStudentRepository.save(groupStudent);

	}

	@Override
	public void deleteById(Long id) {
		GroupStudent groupStudent = groupStudentRepository.findById(id).orElse(null);
		if(groupStudent!=null) {
			groupStudentRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void deleteByClassGroupIdAndStudentId(Long classGroupId, Long studentId) {
		GroupStudent groupStudent = groupStudentRepository.searchByClassGroupIdAndStudentId(classGroupId, studentId);
		if(groupStudent!=null) {
			if (!(classesSessionsRepository.searchByClassGroupId(groupStudent.getClassGroup().getId())).isEmpty()) {
				throw new BadRequestDataException("You cannot unsubscribe a student from a group that already exists on a session");
			}
			groupStudentRepository.deleteById(groupStudent.getId());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		groupStudentRepository.deleteAll();
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
	
}
