package gr.hua.pms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gr.hua.pms.repository.ExcuseApplicationRepository;

@Service
public class ExcuseApplicationServiceImpl implements ExcuseApplicationService {

	@Autowired
	ExcuseApplicationRepository excuseApplicationRepository;
}