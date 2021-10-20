package gr.hua.pms.custom.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CourseScheduleRepositoryCustom {
    void updateCurrentSpringCourseScheduleStatusToActive(String academicYear);
    
	void updatePreviousSpringCourseScheduleStatusToInactive(String academicYear);

	/*void updateSummerCourseScheduleStatusToPending(String academicYear);*/
        
	void updateCurrentWinterCourseScheduleStatusToActive(String academicYear);
	
    void updatePreviousWinterCourseScheduleStatusToInactive(String academicYear);

	/*void updateWinterCourseScheduleStatusToPending(String academicYear);*/
}