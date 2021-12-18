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

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.Lecture;
import gr.hua.pms.payload.request.LectureRequest;
import gr.hua.pms.payload.response.LectureResponse;
import gr.hua.pms.service.LectureService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/lectures")
public class LectureController {

	@Autowired
	LectureService lectureService;
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<List<Lecture>> getAllLecturesSorted(@RequestParam(defaultValue = "id, desc") String[] sort) {
		try {
			List<Lecture> lectures = lectureService.findAll(sort);
			
				if(lectures.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}	
				return new ResponseEntity<>(lectures, HttpStatus.OK);
		} catch(Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllLecturesSortedPaginated(
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {
		
		try {
            Map<String, Object> response = lectureService.findAllSortedPaginated(filter, page, size, sort);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_course-schedule/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllLecturesByCourseScheduleIdSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("ID: "+departmentId);
		try {
            Map<String, Object> response = lectureService.findAllByCourseScheduleIdSortedPaginated(departmentId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_department/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllLecturesByDepartmentIdSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("ID: "+departmentId);
		try {
            Map<String, Object> response = lectureService.findAllByDepartmentSortedPaginated(departmentId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_course-schedule_per_department/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllLecturesByCourseScheduleIdPerDepartmentSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+departmentId);
		System.out.println("Course Schedule Id: "+courseScheduleId);
		try {
            Map<String, Object> response = lectureService.findAllByDepartmentAndCourseScheduleIdSortedPaginated(departmentId, courseScheduleId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_course-schedule_Id_and_type/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or"
			+ " (hasRole('TEACHER') and #userId == authentication.principal.id)")
	public ResponseEntity<Map<String, Object>> getAllLecturesByCourseScheduleIdAndTypeSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType name,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Course Schedule Id: "+courseScheduleId);
		System.out.println("Lecture Type Id: "+courseScheduleId);
		System.out.println("User ID: "+userId);
		
		try {
            Map<String, Object> response = lectureService.findAllByCourseScheduleIdPerTypeSortedPaginated(userId, courseScheduleId, name, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/create/{userId}")
	@PreAuthorize("hasRole('ADMIN') or"
			+ " (hasRole('TEACHER') and #userId == authentication.principal.id)")
	public ResponseEntity<Lecture> createLecture(@RequestBody LectureRequest lectureRequestData,
			@PathVariable("userId") long userId) {
		System.out.println("Lecture to be saved: " + lectureRequestData);
		Lecture _lecture = lectureService.save(lectureRequestData, userId);
		System.out.println("New Lecture here: " + _lecture);
		return new ResponseEntity<>(_lecture, HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}/{userId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Lecture> updateLecture(
			@PathVariable("id") long id,
			@PathVariable("userId") long userId,
			@RequestBody LectureRequest lectureRequestData) {
		return new ResponseEntity<>(lectureService.update(id, userId, lectureRequestData), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}/{userId}")
	@PreAuthorize("hasRole('ADMIN') or"
			+ " (hasRole('TEACHER') and #userId == authentication.principal.id)")
	public ResponseEntity<HttpStatus> deleteLecture(
			@PathVariable("id") long id,
			@PathVariable("userId") long userId) {
		lectureService.deleteById(id, userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<HttpStatus> deleteAllLectures() {
		lectureService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/{id}/{userId}")
	@PreAuthorize("hasRole('ADMIN') or"
			+ " (hasRole('TEACHER') and #userId == authentication.principal.id)")
	public ResponseEntity<LectureResponse> getLectureById(
			@PathVariable("id") long id,
			@PathVariable("userId") long userId) {
		System.out.println("Lecture By Id: " + id);
		System.out.println("User By Id: " + userId);
		LectureResponse lectureResponse = lectureService.findById(id, userId);
		if(lectureResponse!=null) {
			  return new ResponseEntity<>(lectureResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(lectureResponse, HttpStatus.NO_CONTENT);
	}
	
}