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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "course_schedule",
uniqueConstraints = {
		   @UniqueConstraint(columnNames = "course_id")
	})
public class CourseSchedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "academic_year")
	private String academicYear;
	
	@Column(name = "max_theory_lectures")
	private int maxTheoryLectures;
	
	@Column(name = "max_lab_lectures")
	private int maxLabLectures;
	
	@Column(name = "activity_status")
	private Boolean status;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "teaching_stuff",
			   joinColumns = @JoinColumn(name = "course_schedule_id"),
			   inverseJoinColumns = @JoinColumn(name = "teacher_id"))
	private List<User> teachingStuff = new ArrayList<>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "students",
			   joinColumns = @JoinColumn(name = "course_schedule_id"),
			   inverseJoinColumns = @JoinColumn(name = "student_id"))
	private List<User> students = new ArrayList<>();
    
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name ="course_id", referencedColumnName = "id")
	private Course course;
	
}