package gr.hua.pms.payload.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassSessionResponse {

	private Long id;

	private String identifierSuffix;
	
	private String nameIdentifier;
	
	@JsonFormat(pattern = "MM/dd/yyyy")
	private LocalDate date;
	
	private Boolean presenceStatementStatus;
	
	private Boolean status;
		
	private LectureResponse lecture;
	
	private ClassGroupResponse classGroup;
	
}
