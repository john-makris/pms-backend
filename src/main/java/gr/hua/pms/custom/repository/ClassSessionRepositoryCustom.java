package gr.hua.pms.custom.repository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

@Repository
public interface ClassSessionRepositoryCustom {

    void updateCurrentClassSessionStatusToActive(LocalDateTime currentTimeStamp);
    
	void updatePreviousClassSessionStatusToInactive(LocalDateTime currentTimeStamp);
}
