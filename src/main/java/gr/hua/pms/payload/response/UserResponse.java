package gr.hua.pms.payload.response;

import java.util.Set;

import gr.hua.pms.model.Department;
import gr.hua.pms.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private Long id;
	
    private String username;
    
    private String firstname;
	
    private String lastname;
 
    private String email;
    
    private Set<Role> roles;

    private Department department;

    private Boolean status;

   	private String am;

}
