package gr.hua.pms.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExcuseApplicationResponse {

	private Long id;

	private PresenceResponse absence;
	
	private String reason;
	
	private Boolean status;
	
	@JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
	private LocalDateTime dateTime;
}