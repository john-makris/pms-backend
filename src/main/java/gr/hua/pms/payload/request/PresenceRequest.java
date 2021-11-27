package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;

public class PresenceRequest {

	@NotBlank
	private Boolean status;
	
	@NotBlank
	private Long classSessionId;
	
	@NotBlank
	private Long studentId;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Long getClassSessionId() {
		return classSessionId;
	}

	public void setClassSessionId(Long classSessionId) {
		this.classSessionId = classSessionId;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	
}