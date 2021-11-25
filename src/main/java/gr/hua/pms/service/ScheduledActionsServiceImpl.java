package gr.hua.pms.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gr.hua.pms.helper.DateTimeHelper;
import gr.hua.pms.repository.ClassSessionRepository;
import gr.hua.pms.repository.CourseScheduleRepository;

@Service
@Transactional
public class ScheduledActionsServiceImpl implements ScheduledActionsService {

	@Autowired
	CourseScheduleRepository courseScheduleRepository;
	
	@Autowired
	ClassSessionRepository classSessionRepository;
	
    private long delay = 0;
	
	@Override
    public long getDelay() {
        this.delay = 10000;
        System.out.println("delaying " + this.delay + " milliseconds...");
        return this.delay;
    }
	
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

	@Override
	public void makeClassSessionUpdates() {
		// String dateTimePattern = "yyyy/MM/dd HH:mm:ss";
		
		//DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

		LocalDateTime now = LocalDateTime.now();
				
		//String currentTimeStamp = dateTimeFormatter.format(now);
		
		LocalDateTime formatted = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
		
		System.out.println("<<====================================================================================================>>");
		System.out.println("formatted localDateTime: "+formatted);
		System.out.println("<<====================================================================================================>>");
		System.out.println("formatted TimeStamp: "+Timestamp.valueOf(formatted));
		System.out.println("<<====================================================================================================>>");
		
		System.out.println("Check for updates: ");

		classSessionRepository.updateCurrentClassSessionStatusToActive(formatted);
		classSessionRepository.updatePreviousClassSessionStatusToInactive(formatted);

	}

}
