package gr.hua.pms.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.response.MessageResponse;
import gr.hua.pms.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
			return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	
	@GetMapping("/userById/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<User> getUserById(@PathVariable("userId") long userId) {
		User user = userService.findUserById(userId);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	
	@GetMapping("/user/{username}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
		User user = userService.findUserByUsername(username);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PostMapping("/user/create")
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
	
	@PutMapping("/user/update/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<User> updateUser(@PathVariable("userId") long userId, @RequestBody SignupRequest signupRequest) {
		return new ResponseEntity<>(userService.updateUser(userId, signupRequest), HttpStatus.OK);
	}

	
}