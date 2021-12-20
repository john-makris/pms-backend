package gr.hua.pms.controller;

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

import gr.hua.pms.model.ClassSession;
import gr.hua.pms.payload.request.ClassSessionRequest;
import gr.hua.pms.payload.response.ClassSessionResponse;
import gr.hua.pms.service.ClassSessionService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/classes-sessions")
public class ClassSessionController {
	
	@Autowired
	ClassSessionService classSessionService;

	@GetMapping("all/by_lecture_Id/paginated_sorted_filtered")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<Map<String, Object>> getAllClassesSessionsByLectureIdSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long lectureId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Lecture Id: "+lectureId);
		// System.out.println("Class Group Id: "+classGroupId);

		try {
            Map<String, Object> response = classSessionService.findAllByLectureIdSortedPaginated(userId, lectureId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_lecture_Id_and_status/paginated_sorted_filtered")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<Map<String, Object>> getAllClassesSessionsByLectureIdAndStatusSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long lectureId,
		  @RequestParam(required = true) String status,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Lecture Id: "+lectureId);
		System.out.println("Status: "+status);

		try {
            Map<String, Object> response = classSessionService.findAllByLectureIdAndStatusSortedPaginated(userId, lectureId,
            		status, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_user_Id_and_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllClassesSessionsByUserIdSortedPaginated(
		  @RequestParam(required = true) Boolean status,
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("User Id: "+userId);
		System.out.println("Status: "+status);

		try {
            Map<String, Object> response = classSessionService.findAllByUserIdAndStatusSortedPaginated(userId, status, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("by_studentId_and_status/{studentId}/{status}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ClassSessionResponse> getPresentedClassSessionByStudentIdAndStatus(
			@PathVariable("studentId") Long studentId,
			@PathVariable("status") Boolean status) {
		System.out.println("Student Id: "+studentId);
		System.out.println("Status: "+status);

		ClassSessionResponse classSessionResponse = classSessionService.findPresentedClassSessionResponseByStudentIdAndStatus(studentId, status);
		if(classSessionResponse!=null) {
			  return new ResponseEntity<>(classSessionResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(classSessionResponse, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("class_session/{id}/{userId}")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<ClassSessionResponse> getClassSessionById(
			@PathVariable("id") long id,
			@PathVariable("userId") long userId) {
		ClassSessionResponse classSessionResponse = classSessionService.findClassSessionResponseById(id, userId);
		if(classSessionResponse!=null) {
			  return new ResponseEntity<>(classSessionResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(classSessionResponse, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/{lectureId}/{studentId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<ClassSessionResponse> getClassSessionByLectureIdAndStudentId(
			@PathVariable("lectureId") long lectureId,
			@PathVariable("studentId") long studentId) {
		ClassSessionResponse classSessionResponse = classSessionService.findClassSessionResponseByLectureIdAndStudentId(lectureId, studentId);
		if(classSessionResponse!=null) {
			  return new ResponseEntity<>(classSessionResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(classSessionResponse, HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/{id}/{userId}")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<HttpStatus> deleteClassSession(
			@PathVariable("id") long id,
			@PathVariable("userId") long userId) {
		classSessionService.deleteById(id, userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/create/{userId}")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<ClassSession> createClassSession(
			@PathVariable("userId") long userId,
			@RequestBody ClassSessionRequest classSessionRequestData) {
		System.out.println("ClassSession to be saved: " + classSessionRequestData);
		ClassSession _classSession = classSessionService.save(classSessionRequestData, userId);
		System.out.println("New ClassSession here: " + _classSession);
		return new ResponseEntity<>(_classSession, HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}/{userId}")
	@PreAuthorize("(hasRole('ADMIN') or hasRole('TEACHER'))"
			+ " and #userId == authentication.principal.id")
	public ResponseEntity<ClassSession> updateClassSession(
			@PathVariable("id") long id,
			@PathVariable("userId") long userId,
			@RequestBody ClassSessionRequest classSessionRequestData) {
		System.out.println("Update class session: " + classSessionRequestData);
		System.out.println("User id: " + userId);

		return new ResponseEntity<>(classSessionService.update(id, userId, classSessionRequestData), HttpStatus.OK);
	}
}
