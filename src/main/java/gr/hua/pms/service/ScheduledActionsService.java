package gr.hua.pms.service;

public interface ScheduledActionsService {
	
	void makeSeasonUpdates();
	
	void makeClassSessionUpdates();

	long getDelay();
}