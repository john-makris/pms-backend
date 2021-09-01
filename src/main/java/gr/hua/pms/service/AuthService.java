package gr.hua.pms.service;

import gr.hua.pms.payload.request.LoginRequest;
import gr.hua.pms.payload.response.JwtResponse;

public interface AuthService {

	JwtResponse signinUser(LoginRequest loginRequest);
	
}
