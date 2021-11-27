package gr.hua.pms.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PresenceResponse {
	
	private Long id;
	
	private Boolean status;
	
	private ClassSessionResponse classSession;
	
	private UserResponse student;

}
