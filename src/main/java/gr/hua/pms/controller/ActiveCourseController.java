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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gr.hua.pms.model.ActiveCourse;
import gr.hua.pms.payload.request.ActiveCourseRequest;
import gr.hua.pms.service.ActiveCourseService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/active-courses")
public class ActiveCourseController {

	@Autowired
	ActiveCourseService activeCourseService;
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ActiveCourse> createActiveCourse(
			@RequestParam("studentsFile") MultipartFile studentsFile,
			@RequestParam("activeCourseData") String activeCourseDataJson) {
		
		System.out.println("FILE: " + studentsFile.getOriginalFilename());
		System.out.println("JSON " + activeCourseDataJson);
		
		ObjectMapper objectMapper = new ObjectMapper();
		ActiveCourseRequest activeCourseData;
		try {
			activeCourseData = objectMapper.readValue(activeCourseDataJson, ActiveCourseRequest.class);
			ActiveCourse _activeCourse = activeCourseService.save(activeCourseData, studentsFile);
			System.out.println("New activeCourse here: " + _activeCourse);
			return new ResponseEntity<>(_activeCourse, HttpStatus.CREATED);
		} catch (JsonProcessingException e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ActiveCourse> updateActiveCourse(
			@PathVariable("id") long id,
			@RequestParam(required = false) MultipartFile studentsFile,
			@RequestParam("activeCourseData") String activeCourseDataJson) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		ActiveCourseRequest activeCourseData;
		
		try {
			activeCourseData = objectMapper.readValue(activeCourseDataJson, ActiveCourseRequest.class);
			ActiveCourse _activeCourse = activeCourseService.update(id, activeCourseData, studentsFile);
			return new ResponseEntity<>(_activeCourse, HttpStatus.OK);
		} catch (JsonProcessingException e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
	
	@GetMapping("/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllActiveCoursesSortedPaginated(
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {
		
		try {
            Map<String, Object> response = activeCourseService.findAllSortedPaginated(filter, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("per_course/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllActiveCoursesByCourseIdSortedPaginated(
		  @RequestParam(required = true) Long id,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("ID: "+id);
		try {
            Map<String, Object> response = activeCourseService.findAllByCourseIdSortedPaginated(id, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
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
	
	@GetMapping("/course/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ActiveCourse> getActiveCourseByCourseId(@PathVariable("id") long id) {
		ActiveCourse activeCourse = activeCourseService.findByCourseId(id);
		if(activeCourse!=null) {
			  return new ResponseEntity<>(activeCourse, HttpStatus.OK);
		}
		return new ResponseEntity<>(activeCourse, HttpStatus.NO_CONTENT);
	}
	
}