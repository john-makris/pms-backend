package gr.hua.pms.model;

import java.util.HashSet;
import java.util.Set;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users",
	   uniqueConstraints = {
			   @UniqueConstraint(columnNames = "am"),
			   @UniqueConstraint(columnNames = "username"),
			   @UniqueConstraint(columnNames = "email")
	   })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Nullable
	@Column(name = "am", unique=true)
	private String am;
	
	@NotBlank(message = "Username required")
	@Size(min = 3, max = 25,
	message = "Username must be between 2 and 25 characters")
	@Column(name = "username")
	private String username;
	
	@NotBlank(message = "Email required")
	@Size(max = 20, message="Email could not have up to 20 characters")
    @Email(message = "Email should be valid")
	@Column(name = "email")
	private String email;
	
	@NotBlank(message = "Password required")
	@Size(min = 10, max = 60, message ="Password must be between 10 and 60 characters")
	@Column(name = "password")
	private String password;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles",
			   joinColumns = @JoinColumn(name = "user_id"),
			   inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	@Nullable
	@ManyToOne
	@JoinColumn(name="department_id", referencedColumnName = "id")
	private Department department;
	
	@Nullable
	@Column(name = "status")
	private Boolean status = false;

	public User(@NotBlank @Size(max = 20) String username, @NotBlank @Size(max = 50) String email,
			@NotBlank @Size(max = 120) String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
}