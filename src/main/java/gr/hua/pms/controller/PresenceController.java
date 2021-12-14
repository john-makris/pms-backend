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
import gr.hua.pms.model.Presence;
import gr.hua.pms.payload.request.ManagePresencesRequest;
import gr.hua.pms.payload.request.PresenceRequest;
import gr.hua.pms.payload.response.PresenceResponse;
import gr.hua.pms.service.PresenceService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/presences")
public class PresenceController {
	
	@Autowired
	PresenceService presenceService;
	
	@GetMapping("all/by_class_session_id/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByClassSessionIdSortedPaginated(
		  @RequestParam(required = true) Long classSessionId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Class Session Id: "+classSessionId);

		try {
            Map<String, Object> response = presenceService.findAllByClassSessionIdSortedPaginated(classSessionId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_class_session_id_and_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByClassSessionIdAndStatusSortedPaginated(
		  @RequestParam(required = true) Long classSessionId,
		  @RequestParam(required = true) String status,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Class Session Id: "+classSessionId);
		System.out.println("Status: "+status);

		try {
            Map<String, Object> response = presenceService.findAllByClassSessionIdAndStatusSortedPaginated(classSessionId, status, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_class_session_id_status_and_excuse_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByClassSessionIdStatusAndExcuseStatusSortedPaginated(
		  @RequestParam(required = true) Long classSessionId,
		  @RequestParam(required = true) String status,
		  @RequestParam(required = true) String excuseStatus,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Class Session Id: "+classSessionId);
		System.out.println("Status: "+status);
		System.out.println("Excuse Status: "+excuseStatus);

		try {
            Map<String, Object> response = presenceService.findAllByClassSessionIdStatusAndExcuseStatusSortedPaginated(classSessionId, status, excuseStatus, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_user_id_status_and_excuse_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByUserIdAndStatusSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) String typeOfStatus,
		  @RequestParam(required = true) String excuseStatus,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("User Id: "+userId);

		try {
            Map<String, Object> response = presenceService.findAllAbsencesByUserIdAndStatusSortedPaginated(userId, typeOfStatus, excuseStatus, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_user_id_courseSchedule_id_and_type/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByUserIdCourseScheduleIdAndTypeSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType lectureType,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("User Id: "+userId);
		System.out.println("Course Schedule Id: "+courseScheduleId);
		System.out.println("Type: "+lectureType);

		try {
            Map<String, Object> response = presenceService.findAllByUserIdCourseScheduleIdAndTypeSortedPaginated(
            		userId, courseScheduleId, lectureType, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_user_id_courseSchedule_id_type_and_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByUserIdCourseScheduleIdTypeAndStatusSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType lectureType,
		  @RequestParam(required = true) String typeOfStatus,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("User Id: "+userId);

		try {
            Map<String, Object> response = presenceService.findAllByUserIdCourseScheduleIdTypeAndStatusSortedPaginated(userId, courseScheduleId,
            		lectureType, typeOfStatus, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_user_id_courseSchedule_id_type_status_and_excuse_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllPresencesByAllParametersSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType lectureType,
		  @RequestParam(required = true) String typeOfStatus,
		  @RequestParam(required = true) String excuseStatus,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("User Id: "+userId);

		try {
            Map<String, Object> response = presenceService.findAllByAllParametersSortedPaginated(userId, courseScheduleId,
            		lectureType, typeOfStatus, excuseStatus, filter, page, size, sort);
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
	public ResponseEntity<PresenceResponse> getPresenceById(@PathVariable("id") long id) {
		PresenceResponse presenceResponse = presenceService.findPresenceResponseById(id);
		if(presenceResponse!=null) {
			  return new ResponseEntity<>(presenceResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(presenceResponse, HttpStatus.NO_CONTENT);
	}
	/*
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Presence> createPresence(@RequestBody PresenceRequest presenceRequestData) {
		System.out.println("Presence to be saved: " + presenceRequestData);
		Presence _presence = presenceService.save(presenceRequestData);
		System.out.println("New Presence here: " + _presence);
		return new ResponseEntity<>(_presence, HttpStatus.CREATED);
	}*/
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Presence> updatePresence(@PathVariable("id") long id, @RequestBody PresenceRequest presenceRequestData) {
		return new ResponseEntity<>(presenceService.update(id, presenceRequestData), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<HttpStatus> deletePresence(@PathVariable("id") long id) {
		presenceService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/create_presences")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<List<Presence>> createPresences(@RequestBody ManagePresencesRequest managePresencesRequest) {
		System.out.println("Manage Presences Request: " + managePresencesRequest);
		List<Presence> _presences = presenceService.createPresences(managePresencesRequest);
		System.out.println("New Presences here: " + _presences);
		return new ResponseEntity<>(_presences, HttpStatus.CREATED);
	}
	
	@PutMapping("/update_presences")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<List<Presence>> updatePresences(@RequestBody ManagePresencesRequest managePresencesRequest) {
		System.out.println("Controller Level Spot A: classSessionId"+managePresencesRequest.getClassSessionId());
		return new ResponseEntity<>(presenceService.updatePresences(managePresencesRequest.getClassSessionId()), HttpStatus.OK);
	}

	@PutMapping("/update_presence_status")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Presence> updatePresence(@RequestBody PresenceRequest presenceRequestData) {
		return new ResponseEntity<>(presenceService.updatePresenceStatus(presenceRequestData), HttpStatus.OK);
	}
}
