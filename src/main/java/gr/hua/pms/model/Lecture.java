package gr.hua.pms.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "lecture")
public class Lecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "title")
	private String title;
	
	@NotBlank
	@Size(max = 30)
	@Column(name = "room")
	private ERoom room;
	
	@NotBlank
	@Size(max = 20)
	@Column(name = "duration")
	private String duration; // 3 hours in Course
	
	@NotBlank
	@Size(max = 30)
	@Column(name = "lecture_date")
	private String lectureDate; // startDateTime
	
	@NotBlank
	@Column(name = "lecture_type")
	private LectureType lectureType;
	
	@NotBlank
	@Size(max = 5)
	@Column(name = "excuse_absences_limit")
	private int excuseAbsencesLimit; //course
	
	@NotBlank
	@Size(max = 5)
	@Column(name = "presence_statement_status")
	private Boolean presenceStatementStatus;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "lecture_presences",
			   joinColumns = @JoinColumn(name = "lecture_id"),
			   inverseJoinColumns = @JoinColumn(name = "presence_id"))
	private List<Presence> presenceList = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="course_schedule_id", referencedColumnName = "id")
	private CourseSchedule courseSchedule;
	
}