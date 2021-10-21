package gr.hua.pms.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import gr.hua.pms.service.ScheduledActionsService;

@Configuration
@ComponentScan("package gr.hua.pms.config")
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {

	@Autowired
	ScheduledActionsService scheduledActionsService;
	
    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
    
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
          () -> scheduledActionsService.makeSeasonUpdates(),
          context -> {
              
	    		LocalDateTime now = LocalDateTime.now();
	    		int currentMonth = now.getMonthValue();
	    		int currentDay = now.getDayOfMonth();
	    		int currentYear = now.getYear();
	    		
	    		System.out.println("Current month: "+currentMonth);
	    		
	            Date dateOfUpdates = null;;
				Instant nextExecutionTime = null;

				if (currentMonth == 3 && currentDay == 1 ) {
	    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    		    try {
						dateOfUpdates = sdf.parse(Integer.toString(currentYear)+"/"+"10"+"/"+"01");
					} catch (ParseException e) {
						System.out.println("Parse Exception: "+e.getMessage());
					}	    		    
	    		    System.out.println("Date of Updates: "+dateOfUpdates);
	    		    System.out.println("Instant of Updates: "+dateOfUpdates.toInstant());
	    		}
	    		
	    		if (currentMonth == 10 && currentDay == 1) {
	    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    		    try {
						dateOfUpdates = sdf.parse(Integer.toString(currentYear+1)+"/"+"03"+"/"+"01");
					} catch (ParseException e) {
						System.out.println("Parse Exception: "+e.getMessage());
					}
	    		    System.out.println("Date of Updates: "+dateOfUpdates);
	    		    System.out.println("Instant of Updates: "+dateOfUpdates.toInstant());
	    		}
	    		
	    		if (dateOfUpdates != null) {
	    		    nextExecutionTime = dateOfUpdates.toInstant();
	    		    System.out.println("Next Excecution Time: "+nextExecutionTime);
	                return Date.from(nextExecutionTime);
	    		} else {
	    			return null;
	    		}

          }
        );
	}

}
