package gr.hua.pms.custom.repository;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class ClassSessionRepositoryCustomImpl implements ClassSessionRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void updateCurrentClassSessionStatusToActive(LocalDateTime currentTimeStamp) {
		System.out.println("Current TimeStamp: "+currentTimeStamp);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE ClassSession cs SET cs.status=true WHERE cs.startDateTime<=:currentTimeStamp")
		.setParameter("currentTimeStamp", currentTimeStamp);
		entityManager.close();
	}

	@Override
	public void updatePreviousClassSessionStatusToInactive(LocalDateTime currentTimeStamp) {
		System.out.println("Current TimeStamp: "+currentTimeStamp);
		entityManager.flush();
		entityManager.clear();
		entityManager.createQuery("UPDATE ClassSession cs SET cs.status=true WHERE cs.endDateTime<=:currentTimeStamp")
		.setParameter("currentTimeStamp", currentTimeStamp);
		entityManager.close();
	}

}
