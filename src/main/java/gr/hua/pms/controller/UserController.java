package gr.hua.pms.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.model.ERole;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.response.MessageResponse;
import gr.hua.pms.payload.response.UserResponse;
import gr.hua.pms.repository.UserRepository;
import gr.hua.pms.service.FileService;
import gr.hua.pms.service.RoleService;
import gr.hua.pms.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FileService fileService;
	
	@GetMapping("per_department_role/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllUsersByDepartmentIdAndRoleNameSortedPaginated(
			  @RequestParam(required = true) Long id,
			  @RequestParam(required = true) ERole name,
			  @RequestParam(required = false) String filter,
			  @RequestParam(defaultValue = "0") int page,
			  @RequestParam(defaultValue = "3") int size,
		      @RequestParam(defaultValue = "id,asc") String[] sort) {
    	System.out.println("ID: "+id);
    	System.out.println("ERole: "+name);

		try {
			Map<String, Object> response = userService.findAllByRoleNameAndDepartmentIdSortedPaginated(id, name, filter, page, size, sort);
        	System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("per_course_schedule/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllUsersByCourseScheduleSortedPaginated(
			  @RequestParam(required = true) Long id,
			  @RequestParam(required = false) String filter,
			  @RequestParam(defaultValue = "0") int page,
			  @RequestParam(defaultValue = "3") int size,
		      @RequestParam(defaultValue = "id,asc") String[] sort) {
	  	System.out.println("ID: "+id);

		try {
			Map<String, Object> response = userService.findAllByCourseScheduleSortedPaginated(id, filter, page, size, sort);
      	System.out.println("RESPONSE: "+response);
          if(response==null) {
              return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          }
          return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/per_course_schedule_and_class_group/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllUsersWithoutGroupSortedPaginated(
			  @RequestParam(required = true) Long courseScheduleId,
			  @RequestParam(required = true) Integer classGroupTypeId,
			  @RequestParam(required = false) String filter,
			  @RequestParam(defaultValue = "0") int page,
			  @RequestParam(defaultValue = "3") int size,
		      @RequestParam(defaultValue = "id,asc") String[] sort) {
	  	System.out.println("Course Schedule Id: "+courseScheduleId);
	  	System.out.println("Class Group Type Id: "+classGroupTypeId);

		try {
			Map<String, Object> response = userService.findAllStudentsWithoutGroupSortedPaginated(courseScheduleId, classGroupTypeId, filter, page, size, sort);
      	System.out.println("RESPONSE: "+response);
          if(response==null) {
              return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          }
          return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signupRequest) {
		if(userService.existsByUsername(signupRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}
		
		if(userService.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}
		userService.createUser(signupRequest);
		return ResponseEntity.ok(new MessageResponse("User created successfully!"));
	}
	
	@PostMapping("/create_students")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> createStudents(@RequestParam("file") MultipartFile file) {
		fileService.save(file);
		return ResponseEntity.ok(new MessageResponse("Students created successfully!"));
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> updateUser(@PathVariable("id") long id, @RequestBody SignupRequest signupRequest) {
		return new ResponseEntity<>(userService.updateUser(id, signupRequest), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserResponse>> getAllUsersSorted(@RequestParam(defaultValue = "id, desc") String[] sort) {
		try {
			List<UserResponse> users = userService.findAll(sort);
			
			if(users.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(users, HttpStatus.OK);
		} catch(Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("per_department/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllUsersByDepartmentIdSortedPaginated(
		  @RequestParam(required = true) Long id,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("ID: "+id);
		try {
            Map<String, Object> response = userService.findAllByDepartmentIdSortedPaginated(id, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all/sorted")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllUsersSortedPaginated(
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {
			System.out.println("FILTER COMES IN: "+filter);
		try {
            Map<String, Object> response = userService.findAllSortedPaginated(filter, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("per_role/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllUsersByRoleNameSortedPaginated(
			  @RequestParam(required = true) ERole name,
			  @RequestParam(required = false) String filter,
			  @RequestParam(defaultValue = "0") int page,
			  @RequestParam(defaultValue = "3") int size,
		      @RequestParam(defaultValue = "id,asc") String[] sort) {
		try {
			Map<String, Object> response = userService.findAllByRoleNameSortedPaginated(name, filter, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getUserById(@PathVariable("id") long id) {
		UserResponse user = userService.findById(id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@GetMapping("/user/{username}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getUserByUsername(@PathVariable("username") String username) {
		UserResponse user = userService.findByUsername(username);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) throws Exception {
		userService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteAllUsers() {
		userService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}