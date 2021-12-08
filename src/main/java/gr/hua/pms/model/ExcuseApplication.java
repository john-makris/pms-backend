package gr.hua.pms.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "excuse_application",
uniqueConstraints = {
		   @UniqueConstraint(columnNames = {"presence_id"})
	})
public class ExcuseApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name ="presence_id", referencedColumnName = "id")
	private Presence absence;
	
	@NotBlank
	@Size(max = 200)
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "status")
	private Boolean status;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@Column(name = "date_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime dateTime;
	
	
}