package gr.hua.pms.model;

import java.time.LocalDateTime;

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
@Table(name = "session")
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "name_Identifier")
	private String nameIdentifier;
	
	@Column(name = "session_start_date_time")
	private LocalDateTime sessionStartDateTime;
	
	@Column(name = "session_end_date_time")
	private LocalDateTime sessionEndDateTime;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="lecture_id", referencedColumnName = "id")
	private Lecture lecture;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_group_id", referencedColumnName = "id")
	private ClassGroup classGroup;
}
