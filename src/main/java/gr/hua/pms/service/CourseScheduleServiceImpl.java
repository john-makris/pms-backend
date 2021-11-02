package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

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
import gr.hua.pms.helper.DateTimeHelper;
import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.CourseScheduleRequest;
import gr.hua.pms.payload.response.CourseScheduleResponse;
import gr.hua.pms.repository.CourseRepository;
import gr.hua.pms.repository.CourseScheduleRepository;

@Service
@Transactional
public class CourseScheduleServiceImpl implements CourseScheduleService {

	@Autowired
	CourseScheduleRepository courseScheduleRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	FileService fileService;
	
	@Autowired
	RoleService roleService;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);

		List<CourseSchedule> coursesSchedules = new ArrayList<CourseSchedule>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<CourseSchedule> pageCoursesSchedules = null;
		
		System.out.println("FILTER: "+filter);
		
		pageCoursesSchedules = courseScheduleRepository.searchByStatusAndFilterSortedPaginated(filter, pagingSort);

		coursesSchedules = pageCoursesSchedules.getContent();

		if(coursesSchedules.isEmpty()) {
			return null;
		}
		
		List<CourseScheduleResponse> coursesSchedulesResponse = createCoursesSchedulesResponse(coursesSchedules);
		
		Map<String, Object> response = new HashMap<>();
		response.put("coursesSchedules", coursesSchedulesResponse);
		response.put("currentPage", pageCoursesSchedules.getNumber());
		response.put("totalItems", pageCoursesSchedules.getTotalElements());
		response.put("totalPages", pageCoursesSchedules.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByCourseDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);

		List<CourseSchedule> coursesSchedules = new ArrayList<CourseSchedule>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<CourseSchedule> pageCoursesSchedules = null;
		
		System.out.println("FILTER: "+filter);
		
		pageCoursesSchedules = courseScheduleRepository.searchPerDepartmentByStatusAndFilterSortedPaginated(id, filter, pagingSort);

		coursesSchedules = pageCoursesSchedules.getContent();

		if(coursesSchedules.isEmpty()) {
			return null;
		}
		
		List<CourseScheduleResponse> coursesSchedulesResponse = createCoursesSchedulesResponse(coursesSchedules);
		
		Map<String, Object> response = new HashMap<>();
		response.put("coursesSchedules", coursesSchedulesResponse);
		response.put("currentPage", pageCoursesSchedules.getNumber());
		response.put("totalItems", pageCoursesSchedules.getTotalElements());
		response.put("totalPages", pageCoursesSchedules.getTotalPages());

		return response;
	}
	
	@Override
	public List<CourseSchedule> findAll(String[] sort) throws IllegalArgumentException {
		try {
			return courseScheduleRepository.findAll(Sort.by(createOrders(sort)));
		}catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public CourseScheduleResponse findById(Long id) {
		CourseSchedule courseSchedule = courseScheduleRepository.findById(id).orElse(null);
		if (courseSchedule == null) {
			throw new BadRequestDataException("Course Schedule with id "+id+" does not exist");
		}
		return createCourseScheduleResponse(courseSchedule);
	}

	@Override
	public CourseSchedule findByCourseId(Long id) throws IllegalArgumentException  {
		CourseSchedule courseSchedule = courseScheduleRepository.findByCourseId(id);
		return courseSchedule;
	}
	
	@Override
	public CourseSchedule save(CourseScheduleRequest courseScheduleRequestData, MultipartFile studentsFile) throws IllegalArgumentException {
		List<User> students = fileService.find(studentsFile);
		
		CourseSchedule courseSchedule = new CourseSchedule();
		System.out.println("Calculate academic Year: "+DateTimeHelper.calcAcademicYear());
		courseSchedule.setMaxTheoryLectures(courseScheduleRequestData.getMaxTheoryLectures());
		courseSchedule.setMaxLabLectures(courseScheduleRequestData.getMaxLabLectures());
		courseSchedule.setAcademicYear(DateTimeHelper.calcAcademicYear());
		courseSchedule.setTheoryLectureDuration(courseScheduleRequestData.getTheoryLectureDuration());
		courseSchedule.setLabLectureDuration(courseScheduleRequestData.getLabLectureDuration());
		courseSchedule.setCourse(courseScheduleRequestData.getCourse());
		courseSchedule.setTeachingStuff(courseScheduleRequestData.getTeachingStuff());
		courseSchedule.setStudents(students);
		
		this.courseScheduleStatusModerator(courseSchedule, courseScheduleRequestData);
		
		if (courseScheduleRepository.existsByCourseIdAndAcademicYear(courseScheduleRequestData.getCourse().getId(), DateTimeHelper.calcAcademicYear())) {
			throw new BadRequestDataException("Course "+courseSchedule.getCourse().getName()+", has already a schedule !");
		}
		return courseScheduleRepository.save(courseSchedule);
	}
	
	@Override
	public CourseSchedule update(Long id, CourseScheduleRequest courseScheduleRequestData, MultipartFile studentsFile) {
		CourseSchedule _courseSchedule = courseScheduleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Course Schedule with id = " + id));
		
		if (courseScheduleRepository.existsByCourseId(courseScheduleRequestData.getCourse().getId()) && 
				courseScheduleRequestData.getCourse().getId() != _courseSchedule.getCourse().getId()) {
			throw new BadRequestDataException("Course "+courseScheduleRequestData.getCourse().getName()+", has already a schedule !");
		}
		
		List<User> students = new ArrayList<User>();
		
		if(studentsFile != null) {
			System.out.println("Students Update file is not null: "+ studentsFile.getOriginalFilename());
			students = fileService.find(studentsFile);
		} else {
			System.out.println("Students Update file is null: "+ studentsFile);
			students = _courseSchedule.getStudents();
		}
		
		_courseSchedule.setMaxTheoryLectures(courseScheduleRequestData.getMaxTheoryLectures());
		_courseSchedule.setMaxLabLectures(courseScheduleRequestData.getMaxLabLectures());
		_courseSchedule.setAcademicYear(_courseSchedule.getAcademicYear());
		_courseSchedule.setCourse(courseScheduleRequestData.getCourse());
		_courseSchedule.setTeachingStuff(courseScheduleRequestData.getTeachingStuff());
		_courseSchedule.setStudents(students);
		_courseSchedule.setStatus(_courseSchedule.getStatus());
		
		return courseScheduleRepository.save(_courseSchedule);
	}
	
	@Override
	public void deleteById(Long id) throws IllegalArgumentException {
		CourseSchedule courseSchedule = courseScheduleRepository.findById(id).orElse(null);
		if(courseSchedule!=null) {
			courseScheduleRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		courseScheduleRepository.deleteAll();
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
	
	public List<CourseScheduleResponse> createCoursesSchedulesResponse(List<CourseSchedule> coursesSchedules) {
		List<CourseScheduleResponse> coursesSchedulesResponse = new ArrayList<CourseScheduleResponse>();
		
		coursesSchedules.forEach(courseSchedule -> {
			CourseScheduleResponse courseScheduleResponse = 
					new CourseScheduleResponse(
							courseSchedule.getId(), 
							courseSchedule.getMaxTheoryLectures(),
							courseSchedule.getMaxLabLectures(),
							courseSchedule.getTheoryLectureDuration(),
							courseSchedule.getLabLectureDuration(),
							courseSchedule.getAcademicYear(),
							courseSchedule.getCourse(),
							this.userService.createUsersResponse(courseSchedule.getTeachingStuff()),
							courseSchedule.getStatus());
			coursesSchedulesResponse.add(courseScheduleResponse);
		});
		
		return coursesSchedulesResponse;
	}
	
	public CourseScheduleResponse createCourseScheduleResponse(CourseSchedule courseSchedule) {
		return new CourseScheduleResponse(
				courseSchedule.getId(), 
				courseSchedule.getMaxTheoryLectures(),
				courseSchedule.getMaxLabLectures(),
				courseSchedule.getTheoryLectureDuration(),
				courseSchedule.getLabLectureDuration(),
				courseSchedule.getAcademicYear(),
				courseSchedule.getCourse(),
				this.userService.createUsersResponse(courseSchedule.getTeachingStuff()),
				courseSchedule.getStatus());
	}
	
	private void courseScheduleStatusModerator(CourseSchedule courseSchedule, CourseScheduleRequest courseScheduleRequestData) {
		boolean isWinterSeason = DateTimeHelper.calcCurrentSeason();
		
		if (isWinterSeason && courseScheduleRequestData.getCourse().getSemester().getSemesterNumber()%2!=0) {
			courseSchedule.setStatus(true);
		} else if (!isWinterSeason && courseScheduleRequestData.getCourse().getSemester().getSemesterNumber()%2==0) {
			courseSchedule.setStatus(true);
		} else {
			courseSchedule.setStatus(null);
		}
	}

}