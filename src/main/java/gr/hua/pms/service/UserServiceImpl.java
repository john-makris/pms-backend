package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.exception.ResourceCannotBeDeletedException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.ERole;
import gr.hua.pms.model.Role;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.repository.DepartmentRepository;
import gr.hua.pms.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);
		
		List<User> users = new ArrayList<User>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageUsers = null;
		
		pageUsers = userRepository.findAll(getUsernameSpecification(filter), pagingSort);
		
		users = pageUsers.getContent();

		if(users.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("users", users);
		response.put("currentPage", pageUsers.getNumber());
		response.put("totalItems", pageUsers.getTotalElements());
		response.put("totalPages", pageUsers.getTotalPages());
		
		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
		
		List<Order> orders = createOrders(sort);
		
		List<User> users = new ArrayList<User>();	

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageUsers = null;
		
		pageUsers = userRepository.findAll(getSpecification(id, filter), pagingSort);
		
		users = pageUsers.getContent();

		if(users.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("users", users);
		response.put("currentPage", pageUsers.getNumber());
		response.put("totalItems", pageUsers.getTotalElements());
		response.put("totalPages", pageUsers.getTotalPages());
		
		return response;
	}
	
	@Override
	public Map<String, Object> findAllByRoleNameSortedPaginated(ERole name, String filter,
			int page, int size, String[] sort) {
		
		String sortProperty = sort[0];
		Boolean sortDirection = false;
		
		if (sort[1].equals("asc")) {
			sortDirection = true;
		}
		
		final List<User> users = new ArrayList<User>();

		Role role = roleService.findRoleByName(name);
		
		if (filter != null) {
			role.getUsers().forEach(user -> {
				if (user.getUsername().toLowerCase().contains(filter.toLowerCase())) {
					users.add(user);
				}
			});
		} else {
			role.getUsers().forEach(user -> {
				users.add(user);
			});
		}
		
		SortDefinition sortDef = new MutableSortDefinition(sortProperty, false, sortDirection);
		 
		PagedListHolder<User> pageUsers = new PagedListHolder<User>(users);
		pageUsers.setSort(sortDef);
		pageUsers.resort();
		pageUsers.setPageSize(size);
		pageUsers.setPage(page);
		
		Map<String, Object> response = new HashMap<>();
		response.put("users", pageUsers.getPageList());
		response.put("currentPage", pageUsers.getPage());
		response.put("totalItems", pageUsers.getNrOfElements());
		response.put("totalPages", pageUsers.getPageCount());
		
		return response;
	}
	
	@Override
	public Map<String, Object> findAllByRoleNameAndDepartmentIdSortedPaginated(Long id, ERole name, String filter,
			int page, int size, String[] sort) {
		String sortProperty = sort[0];
		Boolean sortDirection = false;
		
		if (sort[1].equals("asc")) {
			sortDirection = true;
		}
		
		List<User> users = new ArrayList<User>();

		Role role = roleService.findRoleByName(name);
		if (filter != null) {
			role.getUsers().forEach(user -> {
				if(user.getDepartment() != null) {
					if (user.getDepartment().getId() == id && user.getUsername().toLowerCase().contains(filter.toLowerCase())) {
						users.add(user);
					}
				}
			});
		} else {
			role.getUsers().forEach(user -> {
				if(user.getDepartment() != null) {
					if (user.getDepartment().getId().equals(id)) {
						users.add(user);
					}
				}
			});
			System.out.println("Danger Users: "+users);
		}
		System.out.println("Danger Users: "+users);

		SortDefinition sortDef = new MutableSortDefinition(sortProperty, false, sortDirection);
		 
		PagedListHolder<User> pageUsers = new PagedListHolder<User>(users);
		pageUsers.setSort(sortDef);
		pageUsers.resort();
		pageUsers.setPageSize(size);
		pageUsers.setPage(page);
		
		Map<String, Object> response = new HashMap<>();
		response.put("users", pageUsers.getPageList());
		response.put("currentPage", pageUsers.getPage());
		response.put("totalItems", pageUsers.getNrOfElements());
		response.put("totalPages", pageUsers.getPageCount());
		
		return response;
	}
	
	@Override
	public User findById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId));
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found user with username = " + username));
	}

	@Override
	public List<User> findAll(String[] sort) {
	    List<User> users = new ArrayList<User>();
	    userRepository.findAll().forEach(users::add);
	    return users;
	}
	
	@Override
	public Boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	@Override
	public void createUser(SignupRequest signupRequest) {
		User user = new User(signupRequest.getUsername(),
				 signupRequest.getEmail(),
				 passwordEncoder.encode(signupRequest.getPassword()));
		
		if(userRepository.findAll().isEmpty()) {
			user.setStatus(null);
		} 
		
		Set<Role> roles = this.roleService.giveRoles(signupRequest);

		user.setRoles(roles);
		user.setDepartment(signupRequest.getDepartment());
		user.setStatus(signupRequest.getStatus());
		try {
			userRepository.save(user);
		} catch(IllegalArgumentException ex) {
			logger.error("IllegalArgumentException: ", ex.getMessage());
		}
	}
	
	@Override
	public User updateUser(Long userId, SignupRequest signupRequest) {
		User _user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId));
		_user.setUsername(signupRequest.getUsername());
		_user.setEmail(signupRequest.getEmail());
		_user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		_user.setDepartment(signupRequest.getDepartment());
		_user.setStatus(signupRequest.getStatus());
		
		Set<Role> roles = this.roleService.giveRoles(signupRequest);

		_user.setRoles(roles);
		try {
			userRepository.save(_user);
		} catch(IllegalArgumentException ex) {
			logger.error("IllegalArgumentException: ", ex.getMessage());
		}
		return null;
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
          // sort=[direction, field]
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
	public void deleteById(Long id) {
		User user = userRepository.findById(id).orElse(null);
		if(user!=null) {
			try {
				userRepository.deleteById(id);
			} catch(Exception ex) {
				throw new ResourceCannotBeDeletedException("You should first delete user's Departments !");
			}
		} else {
			throw new ResourceNotFoundException("Not found School with id = " + id);
		}
	}

	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}
	
	private Specification<User> getUsernameSpecification(String filter)
	{
		//Build Specification with Employee Id and Filter Text
		return (root, criteriaQuery, criteriaBuilder) ->
		{
			criteriaQuery.distinct(true);

			if (isNotNullOrEmpty(filter))
			{
				//Predicate for Employee Projects data
				Predicate predicateForData = criteriaBuilder.or(
						criteriaBuilder.like(root.get("username"), "%" + filter + "%"));

				//Combine both predicates
				return criteriaBuilder.and(predicateForData);
			}
			return null;
		};
	}
	
	private Specification<User> getSpecification(Long id, String filter)
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
						criteriaBuilder.like(root.get("username"), "%" + filter + "%"));

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