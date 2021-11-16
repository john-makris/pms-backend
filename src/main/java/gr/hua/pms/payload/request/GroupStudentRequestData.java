package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;

import gr.hua.pms.model.ClassGroup;

public class GroupStudentRequestData {

	@NotBlank
	private ClassGroup classGroup;
	
	@NotBlank
	private Long studentId;

	public ClassGroup getClassGroup() {
		return classGroup;
	}

	public void setClassGroup(ClassGroup classGroup) {
		this.classGroup = classGroup;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	
}
