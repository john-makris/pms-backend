package gr.hua.pms.service;

import java.util.List;
import java.util.Set;

import gr.hua.pms.model.ERole;
import gr.hua.pms.model.Role;
import gr.hua.pms.payload.request.SignupRequest;

public interface RoleService {
	
	public List<Role> findAll();

	public Role findRoleByName(ERole eRole);
	
	public void createRoles();
	
	public Set<Role> giveRoles(SignupRequest signupRequest);
}
