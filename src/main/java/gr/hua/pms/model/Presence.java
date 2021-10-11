package gr.hua.pms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "presence")
public class Presence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 30)
	@Column(name = "presence_date")
	private String presenceDate; //timestamp
	
	@NotBlank
	@Size(max = 30)
	@Column(name = "presence_status")
	private Boolean presenceStatus;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name ="lecture_id", referencedColumnName = "id")
	private Lecture lecture;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name ="student_id", referencedColumnName = "id")
	private User student;
}