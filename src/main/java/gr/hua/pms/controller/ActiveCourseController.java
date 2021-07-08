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

import gr.hua.pms.model.ActiveCourse;
import gr.hua.pms.service.ActiveCourseService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/activeCourses")
public class ActiveCourseController {

	@Autowired
	ActiveCourseService activeCourseService;
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ActiveCourse> createActiveCourse(@RequestBody ActiveCourse activeCourse) {
		ActiveCourse _activeCourse = activeCourseService.save(activeCourse);
		System.out.println("New activeCourse here: " + _activeCourse);
		return new ResponseEntity<>(_activeCourse, HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ActiveCourse> updateActiveCourse(@PathVariable("id") long id, @RequestBody ActiveCourse activeCourse) {
		return new ResponseEntity<>(activeCourseService.update(id, activeCourse), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<HttpStatus> deleteActiveCourse(@PathVariable("id") long id) {
		activeCourseService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<HttpStatus> deleteAllActiveCourses() {
		activeCourseService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<List<ActiveCourse>> getAllActiveCoursesSorted(@RequestParam(defaultValue = "id, desc") String[] sort) {
		try {
			List<ActiveCourse> activeCourses = activeCourseService.findAll(sort);
			
				if(activeCourses.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}	
				return new ResponseEntity<>(activeCourses, HttpStatus.OK);
		} catch(Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all/sorted")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllActiveCourses(
		  @RequestParam(required = false) String name,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {
		
		try {
            Map<String, Object> response = activeCourseService.findAllSorted(name, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ActiveCourse> getActiveCourseById(@PathVariable("id") long id) {
		ActiveCourse activeCourse = activeCourseService.findById(id);
		if(activeCourse!=null) {
			  return new ResponseEntity<>(activeCourse, HttpStatus.OK);
		}
		return new ResponseEntity<>(activeCourse, HttpStatus.NO_CONTENT);
	}
	
}