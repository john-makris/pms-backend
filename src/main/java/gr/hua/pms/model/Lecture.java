package gr.hua.pms.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "lecture",
		uniqueConstraints = {
				   @UniqueConstraint(columnNames = {"name_identifier", "course_schedule_id"})
				   })
public class Lecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "name_identifier")
	private String nameIdentifier;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "title")
	private String title;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="lecture_type_id", referencedColumnName = "id")
	private LectureType lectureType;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="course_schedule_id", referencedColumnName = "id")
	private CourseSchedule courseSchedule;
	
}