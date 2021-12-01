package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;

public class ManagePresencesRequest {

	@NotBlank
	Long classSessionId;

	public Long getClassSessionId() {
		return classSessionId;
	}

	public void setClassSessionId(Long classSessionId) {
		this.classSessionId = classSessionId;
	}
	
}
