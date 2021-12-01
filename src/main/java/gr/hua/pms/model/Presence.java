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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "presence",
uniqueConstraints = {
		   @UniqueConstraint(columnNames = {"student_id", "class_session_id"})
	})
public class Presence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "status")
	private Boolean status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name ="class_session_id", referencedColumnName = "id")
	private ClassSession classSession;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name ="student_id", referencedColumnName = "id")
	private User student;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@Column(name = "presence_statement_date_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime presenceStatementDateTime;

}