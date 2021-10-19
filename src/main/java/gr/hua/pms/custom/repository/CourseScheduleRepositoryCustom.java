package gr.hua.pms.custom.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CourseScheduleRepositoryCustom {
    void updateSummerCourseScheduleStatusToActive(String academicYear);
    
	void updateSummerCourseScheduleStatusToInactive(String academicYear);

	/*void updateSummerCourseScheduleStatusToPending(String academicYear);*/
        
	void updateWinterCourseScheduleStatusToActive(String academicYear);
	
    void updateWinterCourseScheduleStatusToInactive(String academicYear);

	/*void updateWinterCourseScheduleStatusToPending(String academicYear);*/
}