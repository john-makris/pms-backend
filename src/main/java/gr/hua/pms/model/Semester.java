package gr.hua.pms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "semester")
public class Semester {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 20)
	private String semesterName;
	
	@Column(length = 20)
	private Integer semesterNumber;

	public Semester(String semesterName, Integer semesterNumber) {
		this.semesterName = semesterName;
		this.semesterNumber = semesterNumber;
	}

}