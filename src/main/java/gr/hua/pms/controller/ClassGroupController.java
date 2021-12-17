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

import gr.hua.pms.model.ClassGroup;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.payload.request.ClassGroupRequest;
import gr.hua.pms.payload.response.ClassGroupResponse;
import gr.hua.pms.service.ClassGroupService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/classes-groups")
public class ClassGroupController {

	@Autowired
	ClassGroupService classGroupService;
	
	@GetMapping("all/by_course-schedule_per_department/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllClassesGroupsByCourseScheduleIdPerDepartmentSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+departmentId);
		System.out.println("Course Schedule Id: "+courseScheduleId);
		try {
            Map<String, Object> response = classGroupService.findAllByDepartmentAndCourseScheduleIdSortedPaginated(departmentId, courseScheduleId, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/by_course-scheduleId_and_type/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or"
			+ " (hasRole('TEACHER') and #userId == authentication.principal.id)")
	public ResponseEntity<Map<String, Object>> getAllClassesGroupsByCourseScheduleIdAndTypeSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType name,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Search by courseScheduleId and type: ");
		System.out.println("Course Schedule Id: "+courseScheduleId);
		System.out.println("Type: "+name);
		try {
            Map<String, Object> response = classGroupService.findAllByCourseScheduleIdPerTypeSortedPaginated(userId, courseScheduleId, name, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all/_by_course_scheduleId_and_type_and_status/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllClassesGroupsByCourseScheduleIdAndTypeAndStatusSortedPaginated(
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) ELectureType name,
		  @RequestParam(required = true) Boolean status,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Course Schedule Id: "+courseScheduleId);
		try {
            Map<String, Object> response = classGroupService.findAllByCourseScheduleIdPerTypeAndStatusSortedPaginated(courseScheduleId, name, status, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<ClassGroup> createClassGroup(@RequestBody ClassGroupRequest classGroupRequestData) {
		System.out.println("ClassGroup to be saved: " + classGroupRequestData);
		ClassGroup _classGroup = classGroupService.save(classGroupRequestData);
		System.out.println("New ClassGroup here: " + _classGroup);
		return new ResponseEntity<>(_classGroup, HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<ClassGroup> updateClassGroup(@PathVariable("id") long id, @RequestBody ClassGroupRequest classGroupRequestData) {
		return new ResponseEntity<>(classGroupService.update(id, classGroupRequestData), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<HttpStatus> deleteClassGroup(@PathVariable("id") long id) {
		classGroupService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<HttpStatus> deleteAllClassesGroups() {
		classGroupService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<ClassGroupResponse> getClassGroupById(@PathVariable("id") long id) {
		ClassGroupResponse classGroupResponse = classGroupService.findById(id);
		if(classGroupResponse!=null) {
			  return new ResponseEntity<>(classGroupResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(classGroupResponse, HttpStatus.NO_CONTENT);
	}
}
