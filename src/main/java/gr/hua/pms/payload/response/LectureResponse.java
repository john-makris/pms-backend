package gr.hua.pms.payload.response;

import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.LectureType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LectureResponse {

	private Long id;
	
	private String title;

	private String identifierSuffix;
	
	private String nameIdentifier;

	private LectureType lectureType;
	
	private CourseSchedule courseSchedule;
}