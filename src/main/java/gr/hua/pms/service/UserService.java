package gr.hua.pms.service;

import java.util.List;
import java.util.Map;

import gr.hua.pms.model.ERole;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.response.UserResponse;
import gr.hua.pms.utils.UserFileData;

public interface UserService {
	
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByRoleNameSortedPaginated(ERole name, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByDepartmentIdSortedPaginated(Long id, String filter, int page, int size, String[] sort);
		
	public Map<String, Object> findAllByRoleNameAndDepartmentIdSortedPaginated(Long id, ERole name, String filter, int page, int size, String[] sort);
	
	public Map<String, Object> findAllByCourseScheduleSortedPaginated(Long id, String filter, int page, int size, String[] sort);

	List<UserResponse> findAll(String[] sort);
		
	UserResponse findById(Long userId);
	
	UserResponse findByUsername(String username);
	
	User findByAm(String am);
	
	Boolean existsByAm(String am);
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);
	
	void createUser(SignupRequest signUpRequest);
		
	void createStudentsFromFile(List<UserFileData> usersFileData);
			
	UserResponse updateUser(Long userId, SignupRequest signupRequest);
	
	public void deleteById(Long id);
	
	public void deleteAll();
	
	public List<UserResponse> createUsersResponse(List<User> users);
	
	public UserResponse createUserResponse(User user);
	
	List<User> findUsersByRole(ERole name);
}