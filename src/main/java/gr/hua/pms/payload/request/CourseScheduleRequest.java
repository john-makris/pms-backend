package gr.hua.pms.payload.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import gr.hua.pms.model.Course;
import gr.hua.pms.model.User;

public class CourseScheduleRequest {
	
	private int maxTheoryLectures;
	
	private int maxLabLectures;
	
	private int theoryLectureDuration;

	private int labLectureDuration;
	
	@NotBlank
	private Course course;

	@NotBlank
	private List<User> teachingStuff = new ArrayList<>();

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

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<User> getTeachingStuff() {
		return teachingStuff;
	}

	public void setTeachingStuff(List<User> teachingStuff) {
		this.teachingStuff = teachingStuff;
	}

	public int getTheoryLectureDuration() {
		return theoryLectureDuration;
	}

	public void setTheoryLectureDuration(int theoryLectureDuration) {
		this.theoryLectureDuration = theoryLectureDuration;
	}

	public int getLabLectureDuration() {
		return labLectureDuration;
	}

	public void setLabLectureDuration(int labLectureDuration) {
		this.labLectureDuration = labLectureDuration;
	}

}