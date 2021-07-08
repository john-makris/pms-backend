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

import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.Department;
import gr.hua.pms.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	DepartmentRepository departmentRepository;
	
	@Override
	public Map<String, Object> findAllSorted(String name, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<Department> departments = new ArrayList<Department>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Department> pageDepartments = null;

		if(name==null) {
			try {
				pageDepartments = departmentRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			pageDepartments = departmentRepository.findByNameContaining(name, pagingSort);
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
		return departmentRepository.save(department);
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
	public void deleteById(Long id) throws IllegalArgumentException {
		Department department = departmentRepository.findById(id).orElse(null);
		if(department!=null) {
			departmentRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		departmentRepository.deleteAll();
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