package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.exception.ResourceAlreadyExistsException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.Course;
import gr.hua.pms.repository.ActiveCourseRepository;
import gr.hua.pms.repository.CourseRepository;
import gr.hua.pms.repository.DepartmentRepository;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	ActiveCourseRepository activeCourseRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<Course> courses = new ArrayList<Course>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Course> pageCourses = null;
		
		Course course = new Course();
		try {
			course.setId(Long.valueOf(filter));
		} catch (Exception e) {
			
		}
		course.setName(filter);
		course.setSemester(filter);

		if(filter==null) {
			try {
				pageCourses = courseRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			/* Build Example and ExampleMatcher object */
			ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
					.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
					.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
					.withMatcher("semester", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
			
			Example<Course> courseExample = Example.of(course, customExampleMatcher);
			
			pageCourses = courseRepository.findAll(courseExample, pagingSort);
			System.out.println("3 "+pageCourses);
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
		
		pageCourses = courseRepository.findAll(getSpecification(id, filter), pagingSort);
		
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
	
	private Specification<Course> getSpecification(Long id, String filter)
	{
		//Build Specification with Employee Id and Filter Text
		return (root, criteriaQuery, criteriaBuilder) ->
		{
			criteriaQuery.distinct(true);
			//Predicate for Employee Id
			Predicate predicateForDepartment = criteriaBuilder.equal(root.get("department"), departmentRepository.findById(id).orElse(null));

			if (isNotNullOrEmpty(filter))
			{
				//Predicate for Employee Projects data
				Predicate predicateForData = criteriaBuilder.or(
						criteriaBuilder.like(root.get("name"), "%" + filter + "%"),
						criteriaBuilder.like(root.get("semester"), "%" + filter + "%"));

				//Combine both predicates
				return criteriaBuilder.and(predicateForDepartment, predicateForData);
			}
			return criteriaBuilder.and(predicateForDepartment);
		};
	}

	public boolean isNotNullOrEmpty(String inputString)
	{
		return inputString != null && !inputString.isBlank() && !inputString.isEmpty() && !inputString.equals("undefined") && !inputString.equals("null") && !inputString.equals(" ");
	}

}
