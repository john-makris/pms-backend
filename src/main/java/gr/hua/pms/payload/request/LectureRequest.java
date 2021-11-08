package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.LectureType;

public class LectureRequest {

	@NotBlank
	private String title;
	
	@NotBlank
	private String identifierSuffix;

	@NotBlank
	private LectureType lectureType;
	
	@NotNull
	private CourseSchedule courseSchedule;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LectureType getLectureType() {
		return lectureType;
	}

	public void setLectureType(LectureType lectureType) {
		this.lectureType = lectureType;
	}

	public CourseSchedule getCourseSchedule() {
		return courseSchedule;
	}

	public void setCourseSchedule(CourseSchedule courseSchedule) {
		this.courseSchedule = courseSchedule;
	}

	public String getIdentifierSuffix() {
		return identifierSuffix;
	}

	public void setIdentifierSuffix(String identifierSuffix) {
		this.identifierSuffix = identifierSuffix;
	}
}