package gr.hua.pms.payload.response;

import java.util.ArrayList;
import java.util.List;

import gr.hua.pms.model.Course;
import gr.hua.pms.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActiveCourseResponse {
	
	private Long id;
	
	private int maxTheoryLectures;
	
	private int maxLabLectures;
	
	private String academicYear;
	
	private Course course;

	private List<User> teachingStuff = new ArrayList<>();
	
	private Boolean status;
}