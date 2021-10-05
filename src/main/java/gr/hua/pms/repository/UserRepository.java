package gr.hua.pms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>  {
	
	Optional<User> findByUsername(String username);
	
	User findByAm(String am);
	
	Optional<User> findByDepartmentName(String departmentName);
	
	Page<User> findAll(Pageable pageable);
	
	Boolean existsByAm(String am);
	
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
	
	@Query(value = "SELECT u FROM User u JOIN u.roles r WHERE u.department.id=:id and (:roleId is null or r.id=:roleId) and (:filter is null or u.username like %:filter%)")
	Page<User> searchPerDepartmentByRoleSortedPaginated(Long id, Integer roleId, String filter, Pageable pageable);
	
	@Query(value = "SELECT u FROM User u JOIN u.roles r WHERE r.id=:roleId and (:filter is null or u.username like %:filter%)")
	Page<User> searchByRoleSortedPaginated(Integer roleId, String filter, Pageable pageable);

}