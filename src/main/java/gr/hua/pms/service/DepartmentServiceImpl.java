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

import gr.hua.pms.exception.ResourceAlreadyExistsException;
import gr.hua.pms.exception.ResourceCannotBeDeletedException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.Department;
import gr.hua.pms.repository.DepartmentRepository;
import gr.hua.pms.repository.SchoolRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	SchoolRepository schoolRepository;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<Department> departments = new ArrayList<Department>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Department> pageDepartments = null;

		Department department = new Department();
		department.setName(filter);
		
		if(filter==null) {
			try {
				pageDepartments = departmentRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			/* Build Example and ExampleMatcher object */
			ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
					.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
			
			Example<Department> departmentExample = Example.of(department, customExampleMatcher);
			
			pageDepartments = departmentRepository.findAll(departmentExample, pagingSort);
			System.out.println("3 "+pageDepartments);
		}
		
		departments = pageDepartments.getContent();

		if(departments.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("departments", departments);
		response.put("currentPage", pageDepartments.getNumber());
		response.put("totalItems", pageDepartments.getTotalElements());
		response.put("totalPages", pageDepartments.getTotalPages());
		
		return response;
	}
	
	@Override
	public Map<String, Object> findAllBySchoolIdSortedPaginated(Long id, String filter, int page, int size, String[] sort) {

		List<Order> orders = createOrders(sort);
		
		List<Department> departments = new ArrayList<Department>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Department> pageDepartments = null;
		
		pageDepartments = departmentRepository.findAll(getSpecification(id, filter), pagingSort);
		
		departments = pageDepartments.getContent();
		
		if(departments.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("departments", departments);
		response.put("currentPage", pageDepartments.getNumber());
		response.put("totalItems", pageDepartments.getTotalElements());
		response.put("totalPages", pageDepartments.getTotalPages());
		
		return response;
	}

	@Override
	public List<Department> findAll(String[] sort) {
		try {
			return departmentRepository.findAll(Sort.by(createOrders(sort)));
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Department findById(Long id) {
		Department department = departmentRepository.findById(id).orElse(null);
		return department;
	}

	@Override
	public Department save(Department department) {
		if (department!=null) {
			if (departmentRepository.existsByName(department.getName())== true) {
				throw new ResourceAlreadyExistsException("Department name "+department.getName()+", is already in use!");
			} else {
				return departmentRepository.save(department);
			}
		} else {
			throw new IllegalArgumentException();
		}	
	}
	
	@Override
	public Department update(Long id, Department department) {
		Department _department = departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Department with id = " + id));
		
		_department.setName(department.getName());
		_department.setSchool(department.getSchool());
		
		return departmentRepository.save(_department);
	}

	@Override
	public void deleteById(Long id) {
		Department department = departmentRepository.findById(id).orElse(null);
		if(department!=null) {
			try {
				departmentRepository.deleteById(id);
			} catch(Exception ex) {
				throw new ResourceCannotBeDeletedException("You should first delete department's courses !");
			}
		} else {
			throw new ResourceNotFoundException("Not found Department with id = " + id);
		}
	}

	@Override
	public void deleteAll() {
		departmentRepository.deleteAll();
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
	
	private Specification<Department> getSpecification(Long id, String filter)
	{
		//Build Specification with Employee Id and Filter Text
		return (root, criteriaQuery, criteriaBuilder) ->
		{
			criteriaQuery.distinct(true);
			//Predicate for Employee Id
			Predicate predicateForSchool = criteriaBuilder.equal(root.get("school"), schoolRepository.findById(id).orElse(null));

			if (isNotNullOrEmpty(filter))
			{
				//Predicate for Employee Projects data
				Predicate predicateForData = criteriaBuilder.or(
						criteriaBuilder.like(root.get("name"), "%" + filter + "%"));

				//Combine both predicates
				return criteriaBuilder.and(predicateForSchool, predicateForData);
			}
			return criteriaBuilder.and(predicateForSchool);
		};
	}

	public boolean isNotNullOrEmpty(String inputString)
	{
		return inputString != null && !inputString.isBlank() && !inputString.isEmpty() && !inputString.equals("undefined") && !inputString.equals("null") && !inputString.equals(" ");
	}

}