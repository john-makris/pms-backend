package gr.hua.pms.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.service.ExcuseApplicationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/excuse-applications")
public class ExcuseApplicationController {

	@Autowired
	ExcuseApplicationService excuseApplicationService;
	
	@GetMapping("all/by_department_Id/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllExcuseApplicationsByDepartmentIdSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+departmentId);

		try {
            Map<String, Object> response = excuseApplicationService.findAllByDepartmentIdSortedPaginated(departmentId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_department_Id_and_courseSchedule_Id/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllExcuseApplicationsByDepartmentIdAndCourseScheduleIdSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+departmentId);
		System.out.println("Course Schedule Id: "+courseScheduleId);

		try {
            Map<String, Object> response = excuseApplicationService.findAllByDepartmentIdAndCourseScheduleIdSortedPaginated(departmentId, courseScheduleId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_department_Id_courseSchedule_id_and_type/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
	public ResponseEntity<Map<String, Object>> getAllExcuseApplicationsByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType name,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+departmentId);
		System.out.println("Course Schedule Id: "+courseScheduleId);
		System.out.println("Type: "+name);

		try {
            Map<String, Object> response = excuseApplicationService.findAllByDepartmentIdCourseScheduleIdAndTypeSortedPaginated(departmentId, courseScheduleId, name, filter, page, size, sort);
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
	public ResponseEntity<Map<String, Object>> getAllExcuseApplicationsByUserIdSortedPaginated(
		  @RequestParam(required = true) Boolean status,
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("User Id: "+userId);
		System.out.println("Status: "+status);

		try {
            Map<String, Object> response = excuseApplicationService.findAllByUserIdAndStatusSortedPaginated(userId, status, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
