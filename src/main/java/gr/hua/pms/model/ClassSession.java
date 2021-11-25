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

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "class_session")
public class ClassSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "name_Identifier")
	private String nameIdentifier;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm")
	@Column(name = "start_date_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime startDateTime;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm")
	@Column(name = "end_date_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime endDateTime;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="lecture_id", referencedColumnName = "id")
	private Lecture lecture;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_group_id", referencedColumnName = "id")
	private ClassGroup classGroup;
	
	@Column(name = "presence_statement_status")
	private Boolean presenceStatementStatus;
	
	@Column(name = "status")
	private Boolean status;
}
