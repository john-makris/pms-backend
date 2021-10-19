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
import gr.hua.pms.exception.ResourceAlreadyExistsException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.helper.DateTimeHelper;
import gr.hua.pms.model.Course;
import gr.hua.pms.repository.CourseScheduleRepository;
import gr.hua.pms.repository.CourseRepository;
import gr.hua.pms.repository.DepartmentRepository;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	CourseScheduleRepository activeCourseRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {

		List<Order> orders = createOrders(sort);
		
		List<Course> courses = new ArrayList<Course>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Course> pageCourses = null;

		pageCourses = courseRepository.searchCoursesByFilterSortedPaginated(filter, pagingSort);
		
		courses = pageCourses.getContent();

		if(courses.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("courses", courses);
		response.put("currentPage", pageCourses.getNumber());
		response.put("totalItems", pageCourses.getTotalElements());
		response.put("totalPages", pageCourses.getTotalPages());
		
		return response;
	}
	
	@Override
	public Map<String, Object> findAllBySeasonSortedPaginated(String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<Course> courses = new ArrayList<Course>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Course> pageCourses = null;

		boolean isWinterSeason = DateTimeHelper.calcCurrentSeason();
		
		if (isWinterSeason) {
			pageCourses = courseRepository.searchSummerCoursesByFilterSortedPaginated(filter, pagingSort);
		} else {
			pageCourses = courseRepository.searchWinterCoursesByFilterSortedPaginated(filter, pagingSort);
		}
		
		courses = pageCourses.getContent();

		if(courses.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("courses", courses);
		response.put("currentPage", pageCourses.getNumber());
		response.put("totalItems", pageCourses.getTotalElements());
		response.put("totalPages", pageCourses.getTotalPages());
		
		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<Course> courses = new ArrayList<Course>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Course> pageCourses = null;
		
		pageCourses = courseRepository.searchCoursesPerDepartmentByFilterSortedPaginated(id, filter, pagingSort);

		courses = pageCourses.getContent();

		if(courses.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("courses", courses);
		response.put("currentPage", pageCourses.getNumber());
		response.put("totalItems", pageCourses.getTotalElements());
		response.put("totalPages", pageCourses.getTotalPages());
		
		return response;
	}
	
	
	@Override
	public Map<String, Object> findAllByDepartmentIdAndSeasonSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<Course> courses = new ArrayList<Course>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Course> pageCourses = null;
		
		boolean isWinterSeason = DateTimeHelper.calcCurrentSeason();
		
		if (isWinterSeason) {
			pageCourses = courseRepository.searchSummerCoursesPerDepartmentByFilterSortedPaginated(id, filter, pagingSort);
		} else {
			pageCourses = courseRepository.searchWinterCoursesPerDepartmentByFilterSortedPaginated(id, filter, pagingSort);
		}

		courses = pageCourses.getContent();

		if(courses.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("courses", courses);
		response.put("currentPage", pageCourses.getNumber());
		response.put("totalItems", pageCourses.getTotalElements());
		response.put("totalPages", pageCourses.getTotalPages());
		
		return response;
	}
	
	@Override
	public Course findById(Long id) throws IllegalArgumentException {
		Course course = courseRepository.findById(id).orElse(null);
		return course;
	}

	@Override
	public Course save(Course course) {
		if (course!=null) {
			if (courseRepository.existsByName(course.getName())== true) {
				throw new ResourceAlreadyExistsException("Course name "+course.getName()+", is already in use!");
			} else {
				return courseRepository.save(course);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Course update(Long id, Course course) {
		Course _course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Course with id = " + id));
		
		_course.setName(course.getName());
		_course.setSemester(course.getSemester());
		_course.setDepartment(course.getDepartment());
		
		return courseRepository.save(_course);
	}

	@Override
	public void deleteById(Long id) throws IllegalArgumentException {
		Course course = courseRepository.findById(id).orElse(null);
		if(course!=null) {
			if (activeCourseRepository.findByCourseId(id) != null) {
				throw new BadRequestDataException("You cannot delete course "+course.getName()+" since "
						+ "it has an Active Course");
			} else {
				courseRepository.deleteById(id);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		courseRepository.deleteAll();
	}

	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
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
	public List<Course> findAll(String[] sort) throws IllegalArgumentException {
		try {
			return courseRepository.findAll(Sort.by(createOrders(sort)));
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

}
