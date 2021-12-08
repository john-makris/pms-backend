package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ExcuseApplicationRequest {

	@NotBlank
	private Long absenceId;
	
	@NotBlank
	@Size(max = 200)
	private String reason;

	private Boolean status;

	public Long getAbsenceId() {
		return absenceId;
	}

	public void setAbsenceId(Long absenceId) {
		this.absenceId = absenceId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}