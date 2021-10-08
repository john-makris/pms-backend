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
import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.ActiveCourse;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.ActiveCourseRequest;
import gr.hua.pms.payload.response.ActiveCourseResponse;
import gr.hua.pms.repository.ActiveCourseRepository;
import gr.hua.pms.repository.CourseRepository;

@Service
public class ActiveCourseServiceImpl implements ActiveCourseService {

	@Autowired
	ActiveCourseRepository activeCourseRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	FileService fileService;
	
	@Autowired
	RoleService roleService;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);

		List<ActiveCourse> activeCourses = new ArrayList<ActiveCourse>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ActiveCourse> pageActiveCourses = null;
		
		System.out.println("FILTER: "+filter);
		
		pageActiveCourses = activeCourseRepository.searchByFilterSortedPaginated(filter, pagingSort);

		activeCourses = pageActiveCourses.getContent();

		if(activeCourses.isEmpty()) {
			return null;
		}
		
		List<ActiveCourseResponse> activeCoursesResponse = createActiveCoursesResponse(activeCourses);
		
		Map<String, Object> response = new HashMap<>();
		response.put("activeCourses", activeCoursesResponse);
		response.put("currentPage", pageActiveCourses.getNumber());
		response.put("totalItems", pageActiveCourses.getTotalElements());
		response.put("totalPages", pageActiveCourses.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByCourseDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);

		List<ActiveCourse> activeCourses = new ArrayList<ActiveCourse>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ActiveCourse> pageActiveCourses = null;
		
		System.out.println("FILTER: "+filter);
		
		pageActiveCourses = activeCourseRepository.searchPerDepartmentByFilterSortedPaginated(id, filter, pagingSort);

		activeCourses = pageActiveCourses.getContent();

		if(activeCourses.isEmpty()) {
			return null;
		}
		
		List<ActiveCourseResponse> activeCoursesResponse = createActiveCoursesResponse(activeCourses);
		
		Map<String, Object> response = new HashMap<>();
		response.put("activeCourses", activeCoursesResponse);
		response.put("currentPage", pageActiveCourses.getNumber());
		response.put("totalItems", pageActiveCourses.getTotalElements());
		response.put("totalPages", pageActiveCourses.getTotalPages());

		return response;
	}
	
	@Override
	public List<ActiveCourse> findAll(String[] sort) throws IllegalArgumentException {
		try {
			return activeCourseRepository.findAll(Sort.by(createOrders(sort)));
		}catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public ActiveCourseResponse findById(Long id) {
		ActiveCourse activeCourse = activeCourseRepository.findById(id).orElse(null);
		if (activeCourse == null) {
			throw new BadRequestDataException("Active Course with id "+id+" does not exist");
		}
		return createActiveCourseResponse(activeCourse);
	}

	@Override
	public ActiveCourse findByCourseId(Long id) throws IllegalArgumentException  {
		ActiveCourse activeCourse = activeCourseRepository.findByCourseId(id);
		return activeCourse;
	}
	
	@Override
	public ActiveCourse save(ActiveCourseRequest activeCourseData, MultipartFile studentsFile) throws IllegalArgumentException {
		List<User> students = fileService.find(studentsFile);
		ActiveCourse activeCourse = new ActiveCourse();
		
		activeCourse.setMaxTheoryLectures(activeCourseData.getMaxTheoryLectures());
		activeCourse.setMaxLabLectures(activeCourseData.getMaxLabLectures());
		activeCourse.setAcademicYear(activeCourseData.getAcademicYear());
		activeCourse.setCourse(activeCourseData.getCourse());
		activeCourse.setTeachingStuff(activeCourseData.getTeachingStuff());
		activeCourse.setStudents(students);
		activeCourse.setStatus(activeCourseData.getStatus());
		if (activeCourseRepository.existsByCourseId(activeCourseData.getCourse().getId())) {
			throw new BadRequestDataException("Active Course for Course "+activeCourse.getCourse().getName()+", already exists !");
		}
		return activeCourseRepository.save(activeCourse);
	}
	
	@Override
	public ActiveCourse update(Long id, ActiveCourseRequest activeCourseData, MultipartFile studentsFile) {
		ActiveCourse _activeCourse = activeCourseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found ActiveCourse with id = " + id));
		
		if (activeCourseRepository.existsByCourseId(activeCourseData.getCourse().getId()) && 
				activeCourseData.getCourse().getId() != _activeCourse.getCourse().getId()) {
			throw new BadRequestDataException("Active Course for Course "+activeCourseData.getCourse().getName()+", already exists !");
		}
		
		List<User> students = new ArrayList<User>();
		
		if(studentsFile != null) {
			System.out.println("Students Update file is not null: "+ studentsFile.getOriginalFilename());
			students = fileService.find(studentsFile);
		} else {
			System.out.println("Students Update file is null: "+ studentsFile);
			students = _activeCourse.getStudents();
		}
		
		_activeCourse.setMaxTheoryLectures(activeCourseData.getMaxTheoryLectures());
		_activeCourse.setMaxLabLectures(activeCourseData.getMaxLabLectures());
		_activeCourse.setAcademicYear(activeCourseData.getAcademicYear());
		_activeCourse.setCourse(activeCourseData.getCourse());
		_activeCourse.setTeachingStuff(activeCourseData.getTeachingStuff());
		_activeCourse.setStudents(students);
		_activeCourse.setStatus(activeCourseData.getStatus());
		
		return activeCourseRepository.save(_activeCourse);
	}
	
	@Override
	public void deleteById(Long id) throws IllegalArgumentException {
		ActiveCourse activeCourse = activeCourseRepository.findById(id).orElse(null);
		if(activeCourse!=null) {
			activeCourseRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		activeCourseRepository.deleteAll();
	}

	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);
	    
	    if (sort[0].matches("name")) {
	    	sort[0] = "course.name";
	    }
	    
	    if (sort[0].matches("semester")) {
	    	sort[0] = "course.semester";
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
	
	public List<ActiveCourseResponse> createActiveCoursesResponse(List<ActiveCourse> activeCourses) {
		List<ActiveCourseResponse> activeCoursesResponse = new ArrayList<ActiveCourseResponse>();
		
		activeCourses.forEach(activeCourse -> {
			ActiveCourseResponse activeCourseResponse = 
					new ActiveCourseResponse(
							activeCourse.getId(), 
							activeCourse.getMaxTheoryLectures(),
							activeCourse.getMaxLabLectures(),
							activeCourse.getAcademicYear(),
							activeCourse.getCourse(),
							activeCourse.getTeachingStuff(),
							activeCourse.getStatus());
			activeCoursesResponse.add(activeCourseResponse);
		});
		
		return activeCoursesResponse;
	}
	
	public ActiveCourseResponse createActiveCourseResponse(ActiveCourse activeCourse) {
		return new ActiveCourseResponse(
				activeCourse.getId(), 
				activeCourse.getMaxTheoryLectures(),
				activeCourse.getMaxLabLectures(),
				activeCourse.getAcademicYear(),
				activeCourse.getCourse(),
				activeCourse.getTeachingStuff(),
				activeCourse.getStatus());
	}

}