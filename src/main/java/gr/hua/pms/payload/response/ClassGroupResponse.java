package gr.hua.pms.payload.response;

import gr.hua.pms.model.LectureType;
import gr.hua.pms.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassGroupResponse {

	private Long id;
	
	private String identifierSuffix;
	
	private String nameIdentifier;
	
	private String startTime;
	
	private String endTime;
	
	private int capacity;

	private LectureType lectureType;
	
	private CourseScheduleResponse courseSchedule;
	
	private Room room;
}
