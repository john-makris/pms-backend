package gr.hua.pms.payload.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import gr.hua.pms.model.User;

public class ActiveCourseRequest {
	
	private int maxTheoryLectures;
	
	private int maxLabLectures;
	
	@NotBlank
	private String academicYear;
	
	@NotBlank
	private Long courseId;

	@NotBlank
	private List<User> teachingStuff = new ArrayList<>();

	@NotBlank
	private Boolean status;

	public int getMaxTheoryLectures() {
		return maxTheoryLectures;
	}

	public void setMaxTheoryLectures(int maxTheoryLectures) {
		this.maxTheoryLectures = maxTheoryLectures;
	}

	public int getMaxLabLectures() {
		return maxLabLectures;
	}

	public void setMaxLabLectures(int maxLabLectures) {
		this.maxLabLectures = maxLabLectures;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public List<User> getTeachingStuff() {
		return teachingStuff;
	}

	public void setTeachingStuff(List<User> teachingStuff) {
		this.teachingStuff = teachingStuff;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	

}