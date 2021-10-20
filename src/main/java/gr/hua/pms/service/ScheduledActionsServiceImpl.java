package gr.hua.pms.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gr.hua.pms.helper.DateTimeHelper;
import gr.hua.pms.repository.CourseScheduleRepository;

@Service
@Transactional
public class ScheduledActionsServiceImpl implements ScheduledActionsService {

	@Autowired
	CourseScheduleRepository courseScheduleRepository;
	
	@Override
	public void makeSeasonUpdates() {
		boolean isWinterSeason = DateTimeHelper.calcCurrentSeason();
		
		if (isWinterSeason) {
			courseScheduleRepository.updateCurrentWinterCourseScheduleStatusToActive(DateTimeHelper.calcAcademicYear());
			courseScheduleRepository.updatePreviousSpringCourseScheduleStatusToInactive(DateTimeHelper.calcAcademicYear());
		} else {
			courseScheduleRepository.updateCurrentSpringCourseScheduleStatusToActive(DateTimeHelper.calcAcademicYear());
			courseScheduleRepository.updatePreviousWinterCourseScheduleStatusToInactive(DateTimeHelper.calcAcademicYear());
		}
	}

}
