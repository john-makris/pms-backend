package gr.hua.pms.model;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "class_group")
public class ClassGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "name_Identifier")
	private String nameIdentifier;
	
	@Column(name = "start_time")
	private LocalTime startTime;
	
	@Column(name = "end_time")
	private LocalTime endTime;
	
	@Column(name = "capacity")
	private int capacity;
	
	@Column(name = "status")
	private Boolean status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="group_type_id", referencedColumnName = "id")
	private LectureType groupType;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="course_schedule_id", referencedColumnName = "id")
	private CourseSchedule courseSchedule;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="room_id", referencedColumnName = "id")
	private Room room;
}