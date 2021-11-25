package gr.hua.pms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gr.hua.pms.model.ClassSession;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and cs.classGroup.id=:classGroupId "
			+ "and (:filter is null or cs.nameIdentifier like %:filter% or cs.startDateTime like %:filter%)")
	Page<ClassSession> searchByLectureIdAndClassGroupIdSortedPaginated(
			Long lectureId, Long classGroupId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT cs FROM ClassSession as cs WHERE cs.lecture.id=:lectureId "
			+ "and cs.classGroup.id=:classGroupId and cs.nameIdentifier=:nameIdentifier")
	List<ClassSession> searchByLectureIdAndClassGroupId(Long lectureId, Long classGroupId, String nameIdentifier);
}