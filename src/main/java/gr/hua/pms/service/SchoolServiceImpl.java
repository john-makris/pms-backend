package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import gr.hua.pms.exception.ResourceCannotBeDeletedException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.School;
import gr.hua.pms.repository.SchoolRepository;

@Service
public class SchoolServiceImpl implements SchoolService {

	@Autowired
	SchoolRepository schoolRepository;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<School> schools = new ArrayList<School>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<School> pageSchools = null;
		
		School school = new School();
		school.setName(filter);
		school.setLocation(filter);
		
		if(filter==null) {
			try {
				pageSchools = schoolRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			/* Build Example and ExampleMatcher object */
			ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
					.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
					.withMatcher("location", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
			
			Example<School> schoolExample = Example.of(school, customExampleMatcher);
			
			pageSchools = schoolRepository.findAll(schoolExample, pagingSort);
			System.out.println("3 "+pageSchools);
		}
		
		schools = pageSchools.getContent();

		if(schools.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("schools", schools);
		response.put("currentPage", pageSchools.getNumber());
		response.put("totalItems", pageSchools.getTotalElements());
		response.put("totalPages", pageSchools.getTotalPages());
		
		return response;
	}

	@Override
	public List<School> findAll(String[] sort) throws IllegalArgumentException {
		try {
			return schoolRepository.findAll(Sort.by(createOrders(sort)));
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public School findById(Long id) throws IllegalArgumentException {
		School school = schoolRepository.findById(id).orElse(null);
		return school;
	}

	@Override
	public School save(School school) throws IllegalArgumentException {
		return schoolRepository.save(school);
	}
	
	@Override
	public School update(Long id, School school) {
		School _school = schoolRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found School with id = " + id));
		
		_school.setName(school.getName());
		_school.setLocation(school.getLocation());
		
		return schoolRepository.save(_school);
	}

	@Override
	public void deleteById(Long id) {
		School school = schoolRepository.findById(id).orElse(null);
		if(school!=null) {
			try {
				schoolRepository.deleteById(id);
			} catch(Exception ex) {
				throw new ResourceCannotBeDeletedException("You should first delete school's Departments !");
			}
		} else {
			throw new ResourceNotFoundException("Not found School with id = " + id);
		}
	}

	@Override
	public void deleteAll() {
		schoolRepository.deleteAll();
	}

	@Override
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

}