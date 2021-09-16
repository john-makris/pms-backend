package gr.hua.pms.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.lang.Nullable;

import gr.hua.pms.model.Department;

public class SignupRequest {
	
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String firstname;
	
    @NotBlank
    @Size(min = 3, max = 20)
    private String lastname;
 
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    private Set<String> roles;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    @Nullable
    private Department department;
    
   	@Nullable
    private Boolean status;
   	
   	@Nullable
   	private String am;
   	
    
    public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAm() {
		return am;
	}

	public void setAm(String am) {
		this.am = am;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Boolean getStatus() {
    	return status;
    }
    
    public void setStatus(Boolean status) {
    	this.status = status;
    }
    
    public Department getDepartment() {
    	return department;
    }
    
    public void setDepartment(Department department) {
    	this.department = department;
    }
  
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRoles() {
      return this.roles;
    }
    
    public void setRole(Set<String> role) {
      this.roles = role;
    }
}