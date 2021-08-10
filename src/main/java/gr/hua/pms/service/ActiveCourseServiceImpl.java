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

import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.ActiveCourse;
import gr.hua.pms.repository.ActiveCourseRepository;
import gr.hua.pms.repository.CourseRepository;

@Service
public class ActiveCourseServiceImpl implements ActiveCourseService {

	@Autowired
	ActiveCourseRepository activeCourseRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {

		List<Order> orders = createOrders(sort);

		List<ActiveCourse> activeCourses = new ArrayList<ActiveCourse>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ActiveCourse> pageActiveCourses = null;
		
		ActiveCourse activeCourse = new ActiveCourse();
		activeCourse.setAcademicYear(filter);
		
		if(filter==null) {
			try {
				pageActiveCourses = activeCourseRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			/* Build Example and ExampleMatcher object */
			ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
					.withMatcher("academicYear", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
			
			Example<ActiveCourse> departmentExample = Example.of(activeCourse, customExampleMatcher);
			
			pageActiveCourses = activeCourseRepository.findAll(departmentExample, pagingSort);
			System.out.println("3 "+pageActiveCourses);
		}
		
		activeCourses = pageActiveCourses.getContent();

		if(activeCourses.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("activeCourses", activeCourses);
		response.put("currentPage", pageActiveCourses.getNumber());
		response.put("totalItems", pageActiveCourses.getTotalElements());
		response.put("totalPages", pageActiveCourses.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByCourseIdSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);

		List<ActiveCourse> activeCourses = new ArrayList<ActiveCourse>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<ActiveCourse> pageActiveCourses = null;
		
		pageActiveCourses = activeCourseRepository.findAll(getSpecification(id, filter), pagingSort);
		
		if(activeCourses.isEmpty()) {
			return null;
		}
				
		Map<String, Object> response = new HashMap<>();
		response.put("activeCourses", activeCourses);
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
	public ActiveCourse findById(Long id) throws IllegalArgumentException {
		ActiveCourse activeCourse = activeCourseRepository.findById(id).orElse(null);
		return activeCourse;
	}

	@Override
	public ActiveCourse findByCourseId(Long id) throws IllegalArgumentException  {
		ActiveCourse activeCourse = activeCourseRepository.findByCourseId(id);
		return activeCourse;
	}
	
	@Override
	public ActiveCourse save(ActiveCourse activeCourse) throws IllegalArgumentException {
		return activeCourseRepository.save(activeCourse);
	}

	@Override
	public ActiveCourse update(Long id, ActiveCourse activeCourse) {
		ActiveCourse _activeCourse = activeCourseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found ActiveCourse with id = " + id));
		
		_activeCourse.setTeachingStuff(activeCourse.getTeachingStuff());
		_activeCourse.setStudents(activeCourse.getStudents());
		_activeCourse.setAcademicYear(activeCourse.getAcademicYear());
		_activeCourse.setMaxTheoryLectures(activeCourse.getMaxLabLectures());
		_activeCourse.setMaxLabLectures(activeCourse.getMaxLabLectures());
		_activeCourse.setStatus(activeCourse.getStatus());
		
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
	
	private Specification<ActiveCourse> getSpecification(Long id, String filter)
	{
		//Build Specification with Employee Id and Filter Text
		return (root, criteriaQuery, criteriaBuilder) ->
		{
			criteriaQuery.distinct(true);
			//Predicate for Employee Id
			Predicate predicateForCourse = criteriaBuilder.equal(root.get("course"), courseRepository.findById(id).orElse(null));

			if (isNotNullOrEmpty(filter))
			{
				//Predicate for Employee Projects data
				Predicate predicateForData = criteriaBuilder.or(
						criteriaBuilder.like(root.get("academicYear"), "%" + filter + "%"));

				//Combine both predicates
				return criteriaBuilder.and(predicateForCourse, predicateForData);
			}
			return criteriaBuilder.and(predicateForCourse);
		};
	}

	public boolean isNotNullOrEmpty(String inputString)
	{
		return inputString != null && !inputString.isBlank() && !inputString.isEmpty() && !inputString.equals("undefined") && !inputString.equals("null") && !inputString.equals(" ");
	}

}