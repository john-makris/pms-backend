package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.Lecture;

public class ClassSessionRequest {

	@NotBlank
	private String identifierSuffix;
	
	@NotBlank
	private String date;
	
	@NotBlank
	private Boolean presenceStatementStatus;
		
	@NotBlank
	private Lecture lecture;
	
	@NotBlank
	private ClassGroup classGroup;
	

	public String getIdentifierSuffix() {
		return identifierSuffix;
	}

	public void setIdentifierSuffix(String identifierSuffix) {
		this.identifierSuffix = identifierSuffix;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public Boolean getPresenceStatementStatus() {
		return presenceStatementStatus;
	}

	public void setPresenceStatementStatus(Boolean presenceStatementStatus) {
		this.presenceStatementStatus = presenceStatementStatus;
	}

	public Lecture getLecture() {
		return lecture;
	}

	public void setLecture(Lecture lecture) {
		this.lecture = lecture;
	}

	public ClassGroup getClassGroup() {
		return classGroup;
	}

	public void setClassGroup(ClassGroup classGroup) {
		this.classGroup = classGroup;
	}

}