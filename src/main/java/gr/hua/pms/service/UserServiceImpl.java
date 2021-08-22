package gr.hua.pms.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.jwt.JwtUtils;
import gr.hua.pms.model.ERole;
import gr.hua.pms.model.RefreshToken;
import gr.hua.pms.model.Role;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.LoginRequest;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.response.JwtResponse;
import gr.hua.pms.repository.RoleRepository;
import gr.hua.pms.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired 
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
	@Override
	public JwtResponse loginUser(LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

	    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
	    
	    Instant accessTokenExpiryDate = Instant.now().plusMillis(jwtUtils.getJwtExpirationMs());
	    	    
		return new JwtResponse(
						jwt, 
						refreshToken.getToken(), 
						userDetails.getId(),
						userDetails.getUsername(), 
						userDetails.getEmail(), 
						roles, 
						refreshToken.getExpiryDate(),
						accessTokenExpiryDate);
	}
	
	@Override
	public void createUser(SignupRequest signupRequest) {
		User user = new User(signupRequest.getUsername(),
				 signupRequest.getEmail(),
				 passwordEncoder.encode(signupRequest.getPassword()));
		
		Set<Role> roles = giveRoles(signupRequest);

		user.setRoles(roles);
		try {
			userRepository.save(user);
		} catch(IllegalArgumentException ex) {
			logger.error("IllegalArgumentException: ", ex.getMessage());
		}
	}
	
	@Override
	public Boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public void createRoles() {
		List<Role> roleList = new ArrayList<Role>();
		roleList.add(new Role(ERole.ROLE_STUDENT));
		roleList.add(new Role(ERole.ROLE_ADMIN));
		roleList.add(new Role(ERole.ROLE_TEACHER));
		roleRepository.saveAll(roleList);
	}
	
	public Role findRole(ERole eRole) {
		return roleRepository.findByName(eRole)
				.orElseThrow(() -> new RuntimeException("Error: Role not found"));
	}

	public Set<Role> giveRoles(SignupRequest signupRequest) {
		Set<String> strRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		System.out.println("SignupRequest roles: " + signupRequest.getRoles());
		
		if(roleRepository.findAll().isEmpty()) {
			createRoles();
		}
		
		if (strRoles == null) {
			System.out.println("After strRoles == null -> strRoles: " + strRoles);
			if(userRepository.findAll().isEmpty()) {
				roles.add(findRole(ERole.ROLE_ADMIN));
			} else {
				roles.add(findRole(ERole.ROLE_STUDENT));
			}
		} else {
			System.out.println("Before switch-case -> strRoles: " + strRoles);
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					roles.add(findRole(ERole.ROLE_ADMIN));

					break;
				case "professor":
					System.out.println("Here I am " + strRoles);
					roles.add(findRole(ERole.ROLE_TEACHER));

					break;
				default:
					roles.add(findRole(ERole.ROLE_STUDENT));
				}
			});
		}	
		return roles;
	}

	@Override
	public User updateUser(Long userId, SignupRequest signupRequest) {
		User _user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId));
		_user.setUsername(signupRequest.getUsername());
		_user.setEmail(signupRequest.getEmail());
		_user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		
		Set<Role> roles = giveRoles(signupRequest);

		_user.setRoles(roles);
		try {
			userRepository.save(_user);
		} catch(IllegalArgumentException ex) {
			logger.error("IllegalArgumentException: ", ex.getMessage());
		}
		return null;
	}


	@Override
	public User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found User with id = " + userId));
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Not found user with username = " + username));
	}

	@Override
	public List<User> findAllUsers() {
	    List<User> users = new ArrayList<User>();
	    userRepository.findAll().forEach(users::add);
	    return users;
	}

}