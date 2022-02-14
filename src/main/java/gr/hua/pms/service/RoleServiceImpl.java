package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.model.ERole;
import gr.hua.pms.model.Role;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.repository.RoleRepository;
import gr.hua.pms.repository.UserRepository;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
	@Override
	public Role findRoleByName(ERole eRole) {
		return roleRepository.findByName(eRole)
				.orElseThrow(() -> new RuntimeException("Error: Role not found"));
	}

	@Override
	public List<Role> findAll() {
		try {
			return roleRepository.findAll();
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void createRoles() {
		List<Role> roleList = new ArrayList<Role>();
		roleList.add(new Role(ERole.ROLE_STUDENT));
		roleList.add(new Role(ERole.ROLE_ADMIN));
		roleList.add(new Role(ERole.ROLE_TEACHER));
		roleList.add(new Role(ERole.ROLE_SECRETARY));
		roleRepository.saveAll(roleList);
	}

	@Override
	public Set<Role> giveRoles(SignupRequest signupRequest) {
		Set<String> strRoles = signupRequest.getRoles();
		System.out.println("ROLES: "+strRoles);
		Set<Role> roles = new HashSet<>();
		System.out.println("SignupRequest roles: " + signupRequest.getRoles());
		
		if(roleRepository.findAll().isEmpty()) {
			createRoles();
		}
		
		if (strRoles == null) {
			System.out.println("After strRoles == null -> strRoles: " + strRoles);
			if(userRepository.findAll().isEmpty()) {
				//roles.add(findRoleByName(ERole.ROLE_ADMIN));
				throw new BadRequestDataException("Presence Management System has not yet started, so you cannot sign up !");
			} else {
				roles.add(findRoleByName(ERole.ROLE_TEACHER));
			}
		} else {
			System.out.println("Before switch-case -> strRoles: " + strRoles);
			strRoles.forEach(role -> {
				switch (role) {
				case "ROLE_ADMIN":
					roles.add(findRoleByName(ERole.ROLE_ADMIN));

					break;
				case "ROLE_TEACHER":
					System.out.println("Here I am " + strRoles);
					roles.add(findRoleByName(ERole.ROLE_TEACHER));

					break;
				case "ROLE_SECRETARY":
					System.out.println("Here I am " + strRoles);
					roles.add(findRoleByName(ERole.ROLE_SECRETARY));

					break;
				default:
					roles.add(findRoleByName(ERole.ROLE_STUDENT));
				}
			});
		}	
		return roles;
	}

}