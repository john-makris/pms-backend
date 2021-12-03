package gr.hua.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.pms.service.ExcuseApplicationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/excuse-applications")
public class ExcuseApplicationController {

	@Autowired
	ExcuseApplicationService excuseApplicationService;
}
