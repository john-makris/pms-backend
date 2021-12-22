package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserDetailRequestData {
	
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank
    @Size(min = 10, max = 18)
    private String oldPassword;
	
    @NotBlank
    @Size(min = 10, max = 18)
    private String newPassword;
    
    @NotBlank
    @Size(min = 10, max = 18)
    private String confirmPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
    
}
