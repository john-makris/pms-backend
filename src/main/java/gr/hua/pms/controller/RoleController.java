package gr.hua.pms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.pms.model.Role;
import gr.hua.pms.service.RoleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/roles")
public class RoleController {

	@Autowired
	RoleService roleService;
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Role>> getAllRoles() {
		try {
			List<Role> role = roleService.findAll();
			
				if(role.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}	
				return new ResponseEntity<>(role, HttpStatus.OK);
		} catch(Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}