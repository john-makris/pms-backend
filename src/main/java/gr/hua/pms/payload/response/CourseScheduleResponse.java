package gr.hua.pms.payload.response;

import java.util.ArrayList;
import java.util.List;

import gr.hua.pms.model.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseScheduleResponse {
	
	private Long id;
	
	private int maxTheoryLectures;
	
	private int maxLabLectures;
	
	private Long theoryLectureDuration;

	private Long labLectureDuration;
	
	private String academicYear;
	
	private Course course;

	private List<UserResponse> teachingStuff = new ArrayList<>();
	
	private Boolean status;
}