package gr.hua.pms.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.LectureType;
import gr.hua.pms.model.Room;

public class ClassGroupRequest {

	@NotBlank
	private String identifierSuffix;
	
	@NotBlank
	private String startTime;
	
	@NotNull
	private int capacity;

	@NotBlank
	private LectureType lectureType;
	
	@NotNull
	private CourseSchedule courseSchedule;
	
	@NotNull
	private Room room;

	public String getIdentifierSuffix() {
		return identifierSuffix;
	}

	public void setIdentifierSuffix(String identifierSuffix) {
		this.identifierSuffix = identifierSuffix;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
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

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
}