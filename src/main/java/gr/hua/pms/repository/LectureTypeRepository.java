package gr.hua.pms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.LectureType;

@Repository
public interface LectureTypeRepository extends JpaRepository<LectureType, Long>  {
	Optional<LectureType> findByName(ELectureType name);
}
