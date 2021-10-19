package gr.hua.pms.custom.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class CourseScheduleRepositoryCustomImpl implements CourseScheduleRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void updateSummerCourseScheduleStatusToActive(String academicYear) {
		List<Long> courseIds =
		entityManager.createQuery("SELECT c.id FROM Course c WHERE (c.semester.semesterNumber % 2)=0", Long.class).getResultList();
		entityManager.close();
		System.out.println("Courses: "+courseIds);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE CourseSchedule cs SET cs.status=true WHERE cs.academicYear=:academicYear and cs.course.id IN :courseIds")
		.setParameter("academicYear", academicYear)
		.setParameter("courseIds", courseIds).executeUpdate();
		entityManager.close();
	}

	@Override
	public void updateSummerCourseScheduleStatusToInactive(String academicYear) {
		List<Long> courseIds =
		entityManager.createQuery("SELECT c.id FROM Course c WHERE (c.semester.semesterNumber % 2)=0", Long.class).getResultList();
		entityManager.close();
		System.out.println("Courses: "+courseIds);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE CourseSchedule cs SET cs.status=false WHERE cs.academicYear<:academicYear and cs.course.id IN :courseIds")
		.setParameter("academicYear", academicYear)
		.setParameter("courseIds", courseIds).executeUpdate();
		entityManager.close();
	}

	/*@Override
	public void updateSummerCourseScheduleStatusToPending(String academicYear) {
		List<Long> courseIds =
		entityManager.createQuery("SELECT c.id FROM Course c WHERE (c.semester.semesterNumber % 2)=0", Long.class).getResultList();
		entityManager.close();
		System.out.println("Courses: "+courseIds);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE CourseSchedule cs SET cs.status=null WHERE cs.academicYear>=academicYear and cs.course.id IN :courseIds")
		.setParameter("academicYear", academicYear)
		.setParameter("courseIds", courseIds).executeUpdate();
		entityManager.close();
	}*/

	@Override
	public void updateWinterCourseScheduleStatusToActive(String academicYear) {
		List<Long> courseIds =
		entityManager.createQuery("SELECT c.id FROM Course c WHERE (c.semester.semesterNumber % 2)!=0", Long.class).getResultList();
		entityManager.close();
		System.out.println("Courses: "+courseIds);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE CourseSchedule cs SET cs.status=true WHERE cs.academicYear=:academicYear and cs.course.id IN :courseIds")
		.setParameter("academicYear", academicYear)
		.setParameter("courseIds", courseIds).executeUpdate();
		entityManager.close();
	}

	@Override
	public void updateWinterCourseScheduleStatusToInactive(String academicYear) {
		List<Long> courseIds =
		entityManager.createQuery("SELECT c.id FROM Course c WHERE (c.semester.semesterNumber % 2)!=0", Long.class).getResultList();
		entityManager.close();
		System.out.println("Courses: "+courseIds);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE CourseSchedule cs SET cs.status=false WHERE cs.academicYear<=:academicYear and cs.course.id IN :courseIds")
		.setParameter("academicYear", academicYear)
		.setParameter("courseIds", courseIds).executeUpdate();
		entityManager.close();
	}

	/*@Override
	public void updateWinterCourseScheduleStatusToPending(String academicYear) {
		List<Long> courseIds =
		entityManager.createQuery("SELECT c.id FROM Course c WHERE (c.semester.semesterNumber % 2)!=0", Long.class).getResultList();
		entityManager.close();
		System.out.println("Courses: "+courseIds);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE CourseSchedule cs SET cs.status=null WHERE cs.academicYear>academicYear and cs.course.id IN :courseIds")
		.setParameter("academicYear", academicYear)
		.setParameter("courseIds", courseIds).executeUpdate();
		entityManager.close();
	}*/

}
