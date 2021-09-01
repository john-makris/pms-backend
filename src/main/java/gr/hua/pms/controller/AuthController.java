package gr.hua.pms.controller;

import java.time.Instant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.pms.exception.TokenRefreshException;
import gr.hua.pms.jwt.JwtUtils;
import gr.hua.pms.model.RefreshToken;
import gr.hua.pms.payload.request.LoginRequest;
import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.payload.request.TokenRefreshRequest;
import gr.hua.pms.payload.response.MessageResponse;
import gr.hua.pms.payload.response.TokenRefreshResponse;
import gr.hua.pms.service.AuthService;
import gr.hua.pms.service.RefreshTokenService;
import gr.hua.pms.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/auth")
public class AuthController {

	@Autowired
	AuthService authService;
	
	@Autowired
	UserService userService;
	
    @Autowired
    RefreshTokenService refreshTokenService;
	
    @Autowired
    JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.signinUser(loginRequest));
	}
	
	@PostMapping("/signup")
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
	
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
      String requestRefreshToken = request.getRefreshToken();

      return refreshTokenService.findByToken(requestRefreshToken)
          .map(refreshTokenService::verifyExpiration)
          .map(RefreshToken::getUser)
          .map(user -> {
            String token = jwtUtils.generateTokenFromUsername(user.getUsername());
    	    Instant accessTokenExpiryDate = Instant.now().plusMillis(jwtUtils.getJwtExpirationMs());
    	    
            RefreshToken newRefreshToken = refreshTokenService.findByToken(requestRefreshToken).orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!"));;
            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken, newRefreshToken.getExpiryDate(), accessTokenExpiryDate));
          })
          .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
              "Refresh token is not in database!"));
    }
  
    @DeleteMapping("/logout/{id}")
    public ResponseEntity<?> logoutUser(@PathVariable("id") long id) {
      refreshTokenService.deleteByUserId(id);
		System.out.println("DELETE REFRESH TOKEN: "+id);
      return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
	
}