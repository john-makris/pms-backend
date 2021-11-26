package gr.hua.pms.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "groups_students",
uniqueConstraints = {
		   @UniqueConstraint(columnNames = {"student_id", "class_group_id"})
		   })
public class GroupStudent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="student_id", referencedColumnName = "id")
	private User student;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_group_id", referencedColumnName = "id")
	private ClassGroup classGroup;
	
}