package gr.hua.pms.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExcuseApplicationResponse {

	private Long id;

	private PresenceResponse absence;
	
	private Boolean status;
}
