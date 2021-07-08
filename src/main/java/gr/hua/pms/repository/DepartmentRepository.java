package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	public Department findByName(String name);
	
	Page<Department> findAll(Pageable pageable);
	
	Page<Department> findByNameContaining(String name, Pageable pageable);
	
	List<Department> findByNameContaining(String name, Sort sort);
}