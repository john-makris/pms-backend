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
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<PresenceResponse> getPresenceById(@PathVariable("id") long id) {
		PresenceResponse presenceResponse = presenceService.findPresenceResponseById(id);
		if(presenceResponse!=null) {
			  return new ResponseEntity<>(presenceResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(presenceResponse, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Presence> createPresence(@RequestBody PresenceRequest presenceRequestData) {
		System.out.println("Presence to be saved: " + presenceRequestData);
		Presence _presence = presenceService.save(presenceRequestData);
		System.out.println("New Presence here: " + _presence);
		return new ResponseEntity<>(_presence, HttpStatus.CREATED);
	}
	
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
		return new ResponseEntity<>(presenceService.updatePresences(managePresencesRequest), HttpStatus.OK);
	}

}
