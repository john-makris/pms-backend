package gr.hua.pms.utils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserFileData {
	
    @NotBlank(message = "AM required")
	@Size(min = 5, max = 8,
	message = "AM must be between 5 and 8 integers")
	@Pattern(regexp ="[0-9]+", message = "AM must contain only integer numbers")
    private String am;
    
	@NotBlank(message = "First name required")
	@Size(min = 3, max = 25,
	message = "First name must be between 2 and 25 characters")
    private String firstname;
	
	@NotBlank(message = "Last name required")
	@Size(min = 3, max = 25,
	message = "Last name must be between 2 and 25 characters")
    private String lastname;
    
	@NotBlank(message = "Username required")
	@Size(min = 3, max = 25,
	message = "Username must be between 2 and 25 characters")
    private String username;
 
	@NotBlank(message = "Email required")
	@Size(max = 20, message="Email could not have up to 20 characters")
    @Email(message = "Email should be valid")
    private String email;
    
	@NotBlank(message = "Password required")
	@Size(min = 10, max = 60, message ="Password must be between 10 and 60 characters")
    private String password;
    
	@NotBlank(message = "Department required")
    private String departmentName;
    
    @NotNull(message = "Status required")
    private Boolean status;
    
    @NotNull
    private int fileRowNumber;
    
    public Boolean getStatus() {
    	return status;
    }
    
    public void setStatus(Boolean status) {
    	this.status = status;
    }
    
    public String getDepartmentName() {
    	return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
    	this.departmentName = departmentName;
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

	public String getAm() {
		return am;
	}

	public void setAm(String am) {
		this.am = am;
	}

	public int getFileRowNumber() {
		return fileRowNumber;
	}

	public void setFileRowNumber(int fileRowNumber) {
		this.fileRowNumber = fileRowNumber;
	}

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
    
}