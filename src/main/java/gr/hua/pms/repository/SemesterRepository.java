package gr.hua.pms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
	Optional<Semester> findBySemesterNumber(int semesterNumber);
}