package gr.hua.pms.service;

import java.util.List;

import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.LoginRequest;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.response.JwtResponse;

public interface UserService {
	
	JwtResponse loginUser(LoginRequest loginRequest);
	
	void createUser(SignupRequest signUpRequest);
	
	User updateUser(Long userId, SignupRequest signupRequest);
	
	List<User> findAllUsers();
	
	User findUserById(Long userId);
	
	User findUserByUsername(String username);
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);

}