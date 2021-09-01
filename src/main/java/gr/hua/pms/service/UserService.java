package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ERole;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.SignupRequest;

public interface UserService {
	
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByRoleNameSortedPaginated(ERole name, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByRoleNameAndDepartmentIdSortedPaginated(Long id, ERole name, String filter, int page, int size, String[] sort);

	List<User> findAll(String[] sort);
		
	User findById(Long userId);
	
	User findByUsername(String username);
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);
	
	void createUser(SignupRequest signUpRequest);
			
	User updateUser(Long userId, SignupRequest signupRequest);
	
	public void deleteById(Long id);
	
	public void deleteAll();

}