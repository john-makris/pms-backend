package gr.hua.pms.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.jwt.JwtUtils;
import gr.hua.pms.model.RefreshToken;
import gr.hua.pms.model.User;
import gr.hua.pms.payload.request.LoginRequest;
import gr.hua.pms.payload.response.JwtResponse;
import gr.hua.pms.repository.UserRepository;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired 
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtils jwtUtils;
    
	@Override
	public JwtResponse signinUser(LoginRequest loginRequest) {
		
		User currentUser = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
		
		if (currentUser != null && currentUser.getStatus() != null) {
			if (currentUser.getStatus() != true) {
				throw new BadRequestDataException("Your account is not yet activated");
			}
		}
		
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
						accessTokenExpiryDate,
						userDetails.getDepartment(),
						userDetails.getStatus(),
						userDetails.getAm(),
						userDetails.getFirstname(),
						userDetails.getLastname());
	}

}