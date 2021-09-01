package gr.hua.pms.payload.response;

import java.time.Instant;
import java.util.List;

import gr.hua.pms.model.Department;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String refreshToken;
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private Instant refreshTokenExpiryDate;
	private Instant accessTokenExpiryDate;
	private Department department;
	private Boolean status;

	public JwtResponse(String accessToken, String refreshToken, Long id, String username, 
			String email, List<String> roles, Instant refreshTokenExpiryDate, Instant accessTokenExpiryDate,
			Department department, Boolean status) {
		this.token = accessToken;
		this.refreshToken = refreshToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.accessTokenExpiryDate = accessTokenExpiryDate;
		this.refreshTokenExpiryDate = refreshTokenExpiryDate;
		this.department = department;
		this.status = status;
	}
	
	

	public Department getDepartment() {
		return department;
	}

	public Boolean getStatus() {
		return status;
	}

	public Instant getAccessTokenExpiryDate() {
		return accessTokenExpiryDate;
	}
	
	public void setAccessTokenExpiryDate(Instant accessTokenExpiryDate) {
		this.accessTokenExpiryDate = accessTokenExpiryDate;
	}
	
	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getRefreshToken() {
	    return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
	  this.refreshToken = refreshToken;
	}

	public Instant getRefreshTokenExpiryDate() {
		return refreshTokenExpiryDate;
	}
		
	public void setRefreshTokenExpiryDate(Instant refreshTokenExpiryDate) {
		this.refreshTokenExpiryDate = refreshTokenExpiryDate;
	}
}