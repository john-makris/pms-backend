package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;

public class ExcuseApplicationRequest {

	@NotBlank
	private Long absenceId;
	
	private Boolean status;

	public Long getAbsenceId() {
		return absenceId;
	}

	public void setAbsenceId(Long absenceId) {
		this.absenceId = absenceId;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}