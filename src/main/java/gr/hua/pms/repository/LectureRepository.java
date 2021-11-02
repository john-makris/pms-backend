package gr.hua.pms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gr.hua.pms.model.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
		
	Page<Lecture> findAll(Pageable pageable);
	
	@Query(value = "SELECT l FROM Lecture as l WHERE l.courseSchedule.id=:id and (:filter is null or l.title like %:filter%)")
	Page<Lecture> searchByCourseScheduleAndFilterSortedPaginated(Long id, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT l FROM Lecture as l WHERE :filter is null or l.title like %:filter%")
	Page<Lecture> searchByFilterSortedPaginated(@Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT l FROM Lecture as l WHERE l.courseSchedule.course.department.id=:departmentId and (:filter is null or l.title like %:filter%)")
	Page<Lecture> searchByDepartmentAndFilterSortedPaginated(Long departmentId, @Param("filter") String filter, Pageable pageable);
	
	@Query(value = "SELECT l FROM Lecture as l WHERE l.courseSchedule.course.department.id=:departmentId and l.courseSchedule.id=:courseScheduleId and (:filter is null or l.title like %:filter%)")
	Page<Lecture> searchByCourseSchedulePerDepartmentAndFilterSortedPaginated(Long departmentId, Long courseScheduleId, @Param("filter") String filter, Pageable pageable);
}