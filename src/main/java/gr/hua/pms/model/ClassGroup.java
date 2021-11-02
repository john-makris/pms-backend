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
	
	@Column(name = "class_group_start_time")
	private LocalTime classGroupStartTime;
	
	@Column(name = "class_group_end_time")
	private LocalTime classGroupEndTime;
	
	@Column(name = "capacity")
	private int capacity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="course_schedule_id", referencedColumnName = "id")
	private CourseSchedule courseSchedule;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="room_id", referencedColumnName = "id")
	private Room room;
}