package gr.hua.pms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.pms.model.School;
import gr.hua.pms.service.SchoolService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/schools")
public class SchoolController {

	@Autowired
	SchoolService schoolService;
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<School> createSchool(@RequestBody School school) {
		School _school = schoolService.save(school);
		System.out.println("New school here: " + _school);
		return new ResponseEntity<>(_school, HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<School> updateSchool(@PathVariable("id") long id, @RequestBody School school) {
		return new ResponseEntity<>(schoolService.update(id, school), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteSchool(@PathVariable("id") long id) throws Exception {
		schoolService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteAllSchools() {
		schoolService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<School>> getAllSchools(@RequestParam(defaultValue = "id, desc") String[] sort) {
		try {
			List<School> school = schoolService.findAll(sort);
			
				if(school.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}	
				return new ResponseEntity<>(school, HttpStatus.OK);
		} catch(Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all/sorted")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllSchoolsSorted(
		  @RequestParam(required = false) String name,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {
		
		try {
            Map<String, Object> response = schoolService.findAllSorted(name, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<School> getSchoolById(@PathVariable("id") long id) {
		School school = schoolService.findById(id);
		if(school!=null) {
			  return new ResponseEntity<>(school, HttpStatus.OK);
		}
		return new ResponseEntity<>(school, HttpStatus.NO_CONTENT);
	}
	
}