package gr.hua.pms.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PresenceResponse {
	
	private Long id;
	
	private Boolean status;
	
	private Boolean excuseStatus;
	
	private ClassSessionResponse classSession;
	
	private UserResponse student;
	
	@JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
	private LocalDateTime dateTime;

}
