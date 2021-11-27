package gr.hua.pms.model;

import java.time.LocalDateTime;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "class_session",
uniqueConstraints = {
		   @UniqueConstraint(columnNames = {"lecture_id", "class_group_id"})
	})
public class ClassSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "name_Identifier")
	private String nameIdentifier;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@Column(name = "start_date_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime startDateTime;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@Column(name = "end_date_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime endDateTime;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="lecture_id", referencedColumnName = "id")
	private Lecture lecture;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_group_id", referencedColumnName = "id")
	private ClassGroup classGroup;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "classes_sessions_students",
			   joinColumns = @JoinColumn(name = "class_session_id"),
			   inverseJoinColumns = @JoinColumn(name = "student_id"))
	private List<User> students = new ArrayList<>();
	
	@Column(name = "presence_statement_status")
	private Boolean presenceStatementStatus;
	
	@Column(name = "status")
	private Boolean status;
}
