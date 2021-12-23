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

import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.payload.request.CourseScheduleRequest;
import gr.hua.pms.payload.response.CourseScheduleResponse;
import gr.hua.pms.repository.CourseScheduleRepository;
import gr.hua.pms.service.CourseScheduleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/courses-schedules")
public class CourseScheduleController {

	@Autowired
	CourseScheduleService courseScheduleService;
	
	@Autowired
	CourseScheduleRepository courseScheduleRepository;
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<CourseSchedule>> getAllCoursesSchedulesSorted(@RequestParam(defaultValue = "id, desc") String[] sort) {
		try {
			List<CourseSchedule> coursesSchedules = courseScheduleService.findAll(sort);
			
				if(coursesSchedules.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}	
				return new ResponseEntity<>(coursesSchedules, HttpStatus.OK);
		} catch(Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('SECRETARY')")
	public ResponseEntity<Map<String, Object>> getAllCoursesSchedulesSortedPaginated(
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {
		
		try {
            Map<String, Object> response = courseScheduleService.findAllSortedPaginated(filter, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("per_department/all/paginated_sorted_filtered")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT') or hasRole('SECRETARY'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<Map<String, Object>> getAllCoursesSchedulesByCourseDepartmentIdSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long id,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+id);
		System.out.println("User ID: "+userId);

		try {
            Map<String, Object> response = courseScheduleService.findAllByCourseDepartmentIdSortedPaginated(id, userId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("by_department_Id_and_status/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllCoursesSchedulesByDepartmentIdAndStatusSortedPaginated(
		  @RequestParam(required = true) Long id,
		  @RequestParam(required = true) String status,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("ID: "+id);
		System.out.println("Status: "+status);

		try {
            Map<String, Object> response = courseScheduleService.findAllByDepartmentIdAndStatusSortedPaginated(id, status, filter, page, size, sort);
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
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CourseScheduleResponse> getCourseScheduleById(@PathVariable("id") long id) {
		CourseScheduleResponse courseScheduleResponse = courseScheduleService.findById(id);
		  return new ResponseEntity<>(courseScheduleResponse, HttpStatus.OK);
	}
	
	@GetMapping("/course/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CourseSchedule> getCourseScheduleByCourseId(@PathVariable("id") long id) {
		CourseSchedule courseSchedule = courseScheduleService.findByCourseId(id);
		if(courseSchedule!=null) {
			  return new ResponseEntity<>(courseSchedule, HttpStatus.OK);
		}
		return new ResponseEntity<>(courseSchedule, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CourseSchedule> createCourseSchedule(
			@RequestParam("studentsFile") MultipartFile studentsFile,
			@RequestParam("courseScheduleRequestData") String courseScheduleRequestDataJson) {
		
		System.out.println("FILE: " + studentsFile.getOriginalFilename());
		System.out.println("JSON " + courseScheduleRequestDataJson);
		
		ObjectMapper objectMapper = new ObjectMapper();
		CourseScheduleRequest courseScheduleRequestData;
		try {
			courseScheduleRequestData = objectMapper.readValue(courseScheduleRequestDataJson, CourseScheduleRequest.class);
			CourseSchedule _courseSchedule = courseScheduleService.save(courseScheduleRequestData, studentsFile);
			System.out.println("New courseSchedule here: " + _courseSchedule);
			return new ResponseEntity<>(_courseSchedule, HttpStatus.CREATED);
		} catch (JsonProcessingException e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CourseSchedule> updateCourseSchedule(
			@PathVariable("id") long id,
			@RequestParam(required = false) MultipartFile studentsFile,
			@RequestParam("courseScheduleRequestData") String courseScheduleRequestDataJson) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		CourseScheduleRequest courseScheduleRequestData;
		
		try {
			courseScheduleRequestData = objectMapper.readValue(courseScheduleRequestDataJson, CourseScheduleRequest.class);
			CourseSchedule _courseSchedule = courseScheduleService.update(id, courseScheduleRequestData, studentsFile);
			return new ResponseEntity<>(_courseSchedule, HttpStatus.OK);
		} catch (JsonProcessingException e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteCourseSchedule(@PathVariable("id") long id) {
		courseScheduleService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteAllCoursesSchedules() {
		courseScheduleService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}