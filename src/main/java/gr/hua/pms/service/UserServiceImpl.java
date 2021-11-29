package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.exception.ResourceAlreadyExistsException;
import gr.hua.pms.exception.ResourceCannotBeDeletedException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.Department;
import gr.hua.pms.model.ERole;
import gr.hua.pms.model.Role;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.response.UserResponse;
import gr.hua.pms.repository.DepartmentRepository;
import gr.hua.pms.repository.UserRepository;
import gr.hua.pms.utils.UserFileData;

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
		
		List<UserResponse> usersResponse = createUsersResponse(users);
		
		Map<String, Object> response = new HashMap<>();
		response.put("users", usersResponse);
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
		
		List<UserResponse> usersResponse = createUsersResponse(users);

		Map<String, Object> response = new HashMap<>();
		response.put("users", usersResponse);
		response.put("currentPage", pageUsers.getNumber());
		response.put("totalItems", pageUsers.getTotalElements());
		response.put("totalPages", pageUsers.getTotalPages());
		
		return response;
	}
	
	@Override
	public Map<String, Object> findAllByRoleNameSortedPaginated(ERole name, String filter, int page, int size,
			String[] sort) {
		
    	Integer roleId = null;
    	
    	System.out.println("Find By Role Name");
    	
    	if (name != null) {
    		roleId = roleService.findRoleByName(name).getId();
    	}
    	
    	System.out.println("ROLE ID: "+roleId);
		
		List<Order> orders = createOrders(sort);

		List<User> users = new ArrayList<User>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageUsers = null;
		
		System.out.println("FILTER: "+filter);
		
		pageUsers = userRepository.searchByRoleSortedPaginated(roleId, filter, pagingSort);

		users = pageUsers.getContent();
		
    	System.out.println("USERS: "+users);

		if(users.isEmpty()) {
			return null;
		}
		
		List<UserResponse> usersResponse = createUsersResponse(users);
		
		Map<String, Object> response = new HashMap<>();
		response.put("users", usersResponse);
		response.put("currentPage", pageUsers.getNumber());
		response.put("totalItems", pageUsers.getTotalElements());
		response.put("totalPages", pageUsers.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByRoleNameAndDepartmentIdSortedPaginated(Long id, ERole name, String filter,
			int page, int size, String[] sort) {
		
    	System.out.println("ID: "+id);
    	
    	System.out.println("Find By Role Name and Department ID");

    	Integer roleId = null;
    	
    	if (name != null) {
    		roleId = roleService.findRoleByName(name).getId();
    	}
    	
    	System.out.println("ROLE ID: "+id);
		
		List<Order> orders = createOrders(sort);

		List<User> users = new ArrayList<User>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageUsers = null;
		
		System.out.println("FILTER: "+filter);
		
		pageUsers = userRepository.searchPerDepartmentByRoleSortedPaginated(id, roleId, filter, pagingSort);

		users = pageUsers.getContent();
		
    	System.out.println("USERS: "+users);

		if(users.isEmpty()) {
			return null;
		}
		
		List<UserResponse> usersResponse = createUsersResponse(users);

		Map<String, Object> response = new HashMap<>();
		response.put("users", usersResponse);
		response.put("currentPage", pageUsers.getNumber());
		response.put("totalItems", pageUsers.getTotalElements());
		response.put("totalPages", pageUsers.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByCourseScheduleSortedPaginated(Long id, String filter, int page, int size, String[] sort) {
    	
		System.out.println("Page: "+page);
		
		System.out.println("Size: "+size);
		
		System.out.println("Sort: "+sort);
		
		List<Order> orders = createStudentOrders(sort);

		List<User> students = new ArrayList<User>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageStudents = null;
		
		System.out.println("FILTER: "+filter);
		
		pageStudents = userRepository.searchStudentsPerCourseScheduleByFilterSortedPaginated(id, filter, pagingSort);

		students = pageStudents.getContent();

		System.out.println("STUDENTS: "+students);
		
		if(students.isEmpty()) {
			return null;
		}
		
		List<UserResponse> studentsResponse = createUsersResponse(students);

		Map<String, Object> response = new HashMap<>();
		response.put("users", studentsResponse);
		response.put("currentPage", pageStudents.getNumber());
		response.put("totalItems", pageStudents.getTotalElements());
		response.put("totalPages", pageStudents.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllStudentsWithoutGroupSortedPaginated(Long courseScheduleId, Integer classGroupTypeId,
			String filter, int page, int size, String[] sort) {
    	
		System.out.println("Page: "+page);
		
		System.out.println("Size: "+size);
		
		System.out.println("Sort: "+sort);
		
		List<Order> orders = createStudentOrders(sort);

		List<User> students = new ArrayList<User>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageStudents = null;
		
		System.out.println("FILTER: "+filter);
		
		pageStudents = userRepository.searchStudentsWithoutGroup(courseScheduleId, classGroupTypeId, filter, pagingSort);

		students = pageStudents.getContent();

		System.out.println("STUDENTS: "+students);
		
		if(students.isEmpty()) {
			return null;
		}
		
		List<UserResponse> studentsResponse = createUsersResponse(students);

		Map<String, Object> response = new HashMap<>();
		response.put("users", studentsResponse);
		response.put("currentPage", pageStudents.getNumber());
		response.put("totalItems", pageStudents.getTotalElements());
		response.put("totalPages", pageStudents.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllStudentsByClassSessionIdSortedPaginated(Long classSessionId,
			String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<User> students = new ArrayList<User>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<User> pageStudents = null;

		pageStudents = userRepository.searchStudentsByClassSessionIdSortedPaginated(classSessionId, filter, pagingSort);
		
		students = pageStudents.getContent();

		if(students.isEmpty()) {
			return null;
		}
				
		List<UserResponse> studentsResponse = createUsersResponse(students);

		Map<String, Object> response = new HashMap<>();
		response.put("users", studentsResponse);
		response.put("currentPage", pageStudents.getNumber());
		response.put("totalItems", pageStudents.getTotalElements());
		response.put("totalPages", pageStudents.getTotalPages());

		return response;
	}
	
	@Override
	public UserResponse findUserResponseById(Long userId) {
		return createUserResponse(userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId)));
	}
	
	@Override
	public User findById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId));
	}

	@Override
	public UserResponse findByUsername(String username) {
		return createUserResponse(userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found user with username = " + username)));
	}

	@Override
	public List<UserResponse> findAll(String[] sort) {
	    List<User> users = new ArrayList<User>();
	    userRepository.findAll().forEach(users::add);
	    return createUsersResponse(users);
	}
	
	@Override
	public Boolean existsByAm(String am) {
		return userRepository.existsByAm(am);
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

		user.setFirstname(signupRequest.getFirstname());
		user.setLastname(signupRequest.getLastname());
		user.setRoles(roles);
		user.setDepartment(signupRequest.getDepartment());
		user.setStatus(signupRequest.getStatus());
		user.setAm(signupRequest.getAm());
		try {
			userRepository.save(user);
		} catch(IllegalArgumentException ex) {
			logger.error("IllegalArgumentException: ", ex.getMessage());
		}
	}
	
	@Override
	public void createStudentsFromFile(List<UserFileData> usersFileData) {
				
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		usersFileData.forEach(userFileData -> {
			
			Set<ConstraintViolation<UserFileData>> violations = validator.validate(userFileData);
		    if (!violations.isEmpty()) {
		    	try {
			        throw new ConstraintViolationException(violations);
		    	} catch (Exception ex) {
					throw new BadRequestDataException("Error in row "+userFileData.getFileRowNumber()+": "+ex.getMessage());
		    	}
		    }
		    
			
			if(this.existsByAm(userFileData.getAm())) {
				throw new ResourceAlreadyExistsException("Error in row "+userFileData.getFileRowNumber()+": AM "+userFileData.getAm()+" already exists!");
			}
			
			if(this.existsByUsername(userFileData.getUsername())) {
				throw new ResourceAlreadyExistsException("Error in row "+userFileData.getFileRowNumber()+": Username "+userFileData.getUsername()+" is already taken!");
			}
			
			if(this.existsByEmail(userFileData.getEmail())) {
				throw new ResourceAlreadyExistsException("Error in row "+userFileData.getFileRowNumber()+": Email "+userFileData.getEmail()+" is already in use!");
			}
			
			User user = new User(userFileData.getUsername(),
					userFileData.getEmail(),
					passwordEncoder.encode(userFileData.getPassword()));
			
			Set<Role> roles = new HashSet<>();
			roles.add(this.roleService.findRoleByName(ERole.ROLE_STUDENT));

			Department department = departmentRepository.findByName(userFileData.getDepartmentName())
					.orElseThrow(() -> new ResourceNotFoundException("Error in row "+userFileData.getFileRowNumber()+
							": Department with name "+userFileData.getDepartmentName()+" not found"));
			
			user.setFirstname(userFileData.getFirstname());
			user.setLastname(userFileData.getLastname());
			user.setAm(userFileData.getAm());
			user.setRoles(roles);
			user.setDepartment(department);
			user.setStatus(userFileData.getStatus());
					    			
			try {
				userRepository.save(user);
			} catch(IllegalArgumentException ex) {
				logger.error("IllegalArgumentException: ", ex.getMessage());
			}
		});
	}
	
	@Override
	public UserResponse updateUser(Long userId, SignupRequest signupRequest) {
		User _user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId));
		_user.setFirstname(signupRequest.getFirstname());
		_user.setLastname(signupRequest.getLastname());
		_user.setUsername(signupRequest.getUsername());
		_user.setEmail(signupRequest.getEmail());
		_user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		_user.setDepartment(signupRequest.getDepartment());
		_user.setStatus(signupRequest.getStatus());
		_user.setAm(signupRequest.getAm());
		
		Set<Role> roles = this.roleService.giveRoles(signupRequest);

		_user.setRoles(roles);
		try {
			return createUserResponse(userRepository.save(_user));
		} catch(IllegalArgumentException ex) {
			logger.error("IllegalArgumentException: ", ex.getMessage());
		}
		return null;
	}

	private List<Order> createStudentOrders(String[] sort) {
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);
	    
	    sort[0] = "user."+sort[0];
	    
	    return orderCreator(sort);
	}
	
	private List<Order> createOrders(String[] sort) {
	    return orderCreator(sort);
	}
	
	private List<Order> orderCreator(String[] sort) {
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
			if(userRepository.findCourseScheduleStudentById(id).orElse(null) != null) {
				throw new ResourceCannotBeDeletedException("You should first delete student's Course Schedule!");
			}
			userRepository.deleteById(id);
		} else {
			throw new ResourceNotFoundException("Not found School with id = " + id);
		}
	}

	@Override
	public User findByAm(String am) {
		User user = userRepository.findByAm(am).orElse(null);
		if (user == null) {
    		throw new BadRequestDataException("Student with AM: "+am+", does not exist");
		}
		return user;
	}
	
	@Override
	public User findByDepartmentId(Long departmentId) {
		User user = userRepository.findByDepartmentId(departmentId).orElse(null);
		if (user == null) {
    		throw new BadRequestDataException("User, does not exist on any department");
		}
		return user;
	}
	
	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}
	
	private Specification<User> getUsernameSpecification(String filter)
	{
		return (root, criteriaQuery, criteriaBuilder) ->
		{
			criteriaQuery.distinct(true);

			if (isNotNullOrEmpty(filter))
			{
				Predicate predicateForData = criteriaBuilder.or(
						criteriaBuilder.like(root.get("username"), "%" + filter + "%"));

				return criteriaBuilder.and(predicateForData);
			}
			return null;
		};
	}
	
	private Specification<User> getSpecification(Long id, String filter)
	{
		return (root, criteriaQuery, criteriaBuilder) ->
		{
			criteriaQuery.distinct(true);
			Predicate predicateForDepartment = criteriaBuilder.equal(root.get("department"), departmentRepository.findById(id).orElse(null));

			if (isNotNullOrEmpty(filter))
			{
				Predicate predicateForData = criteriaBuilder.or(
						criteriaBuilder.like(root.get("username"), "%" + filter + "%"));

				return criteriaBuilder.and(predicateForDepartment, predicateForData);
			}
			return criteriaBuilder.and(predicateForDepartment);
		};
	}

	private boolean isNotNullOrEmpty(String inputString) {
		return inputString != null && !inputString.isBlank() && !inputString.isEmpty() && !inputString.equals("undefined") && !inputString.equals("null") && !inputString.equals(" ");
	}
	
	@Override
	public List<UserResponse> createUsersResponse(List<User> users) {
		List<UserResponse> usersResponse = new ArrayList<UserResponse>();
		
		users.forEach(user -> {
			UserResponse userResponse = createUserResponse(user);
			usersResponse.add(userResponse);
		});
		
		return usersResponse;
	}
	
	@Override
	public UserResponse createUserResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getFirstname(),
				user.getLastname(),
				user.getEmail(),
				user.getRoles(),
				user.getDepartment(),
				user.getStatus(),
				user.getAm());
	}

	@Override
	public List<User> findUsersByRole(ERole name) {
		int roleId = roleService.findRoleByName(name).getId();
		System.out.println("Role ID: "+roleId);
		System.out.println("FindByRole: "+userRepository.findByRole(roleId));
		return userRepository.findByRole(roleId);
	}

}