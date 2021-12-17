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
import gr.hua.pms.model.GroupStudent;
import gr.hua.pms.payload.request.GroupStudentRequestData;
import gr.hua.pms.payload.response.UserResponse;
import gr.hua.pms.service.GroupStudentService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/pms/groups-students")
public class GroupStudentController {

	@Autowired
	GroupStudentService groupStudentService;
	
	
	@GetMapping("all/by_course-scheduleId_and_type_and_class-group_per_department/paginated_sorted_filtered")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<Map<String, Object>> getAllGroupsStudentsSortedPaginated(
		  @RequestParam(required = true) Long departmentId,
		  @RequestParam(required = true) Long courseScheduleId,
		  @RequestParam(required = true) Long classGroupId,
		  @RequestParam(required = true) ELectureType name,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Department Id: "+departmentId);
		System.out.println("Course Schedule Id: "+courseScheduleId);
		try {
            Map<String, Object> response = groupStudentService.findAllByDepartmentCourseSchedulePerTypeAndClassGroupSortedPaginated(departmentId, courseScheduleId, classGroupId, name, filter, page, size, sort);
    		System.out.println("RESPONSE: "+response);
            if(response==null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all/students_of_group")
	@PreAuthorize("hasRole('ADMIN') or"
			+ " (hasRole('TEACHER') and #userId == authentication.principal.id)")
	public ResponseEntity<Map<String, Object>> getAllStudentsOfGroupSortedPaginated(
		  @RequestParam(required = true) Long userId,
		  @RequestParam(required = true) Long classGroupId,
		  @RequestParam(required = false) String filter,
		  @RequestParam(defaultValue = "0") int page,
		  @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,asc") String[] sort) {
		System.out.println("Get all Students of Class Group");
		System.out.println("User Id: "+classGroupId);
		System.out.println("Class Group Id: "+classGroupId);
		try {
            Map<String, Object> response = groupStudentService.findStudentsOfGroup(userId, classGroupId, filter, page, size, sort);
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
	public ResponseEntity<GroupStudent> createGroupStudent(@RequestBody GroupStudentRequestData groupStudentRequestData) {
		System.out.println("GroupStudent request data to be saved: " + groupStudentRequestData);
		GroupStudent _groupStudent = groupStudentService.save(groupStudentRequestData);
		System.out.println("New GroupStudent here: " + _groupStudent);
		return new ResponseEntity<>(_groupStudent, HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<GroupStudent> updateGroupStudent(@PathVariable("id") long id, @RequestBody GroupStudent groupStudent) {
		return new ResponseEntity<>(groupStudentService.update(id, groupStudent), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<HttpStatus> deleteGroupStudent(@PathVariable("id") long id) {
		groupStudentService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/{classGroupId}/{studentId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<HttpStatus> deleteGroupStudentByClassGroupIdAndStudentId(
			@PathVariable("classGroupId") long classGroupId, 
			@PathVariable("studentId") long studentId) {
		groupStudentService.deleteByClassGroupIdAndStudentId(classGroupId, studentId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/delete/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<HttpStatus> deleteAllGroupsStudents() {
		groupStudentService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<GroupStudent> getGroupStudentById(@PathVariable("id") long id) {
		GroupStudent groupStudent = groupStudentService.findById(id);
		if(groupStudent!=null) {
			  return new ResponseEntity<>(groupStudent, HttpStatus.OK);
		}
		return new ResponseEntity<>(groupStudent, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/by_student_id_and_classGroup_id/{studentId}/{classGroupId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<UserResponse> getStudentOfGroup(
			@PathVariable("studentId") long studentId,
			@PathVariable("classGroupId") long classGroupId) {
		UserResponse student = groupStudentService.findStudentOfGroup(studentId, classGroupId);
		if(student!=null) {
			  return new ResponseEntity<>(student, HttpStatus.OK);
		}
		return new ResponseEntity<>(student, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/by_student_id/{studentId}/{courseScheduleId}/{groupType}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	public ResponseEntity<ClassGroup> getClassGroupStudentByStudentIdAndGroupType(
			@PathVariable("studentId") long studentId,
			@PathVariable("courseScheduleId") long courseScheduleId,
			@PathVariable("groupType") ELectureType groupType) {
		ClassGroup classGroup = groupStudentService.findClassGroupByStudentIdAndCourseScheduleIdAndGroupType(studentId, courseScheduleId, groupType);
		if(classGroup!=null) {
			  return new ResponseEntity<>(classGroup, HttpStatus.OK);
		}
		return new ResponseEntity<>(classGroup, HttpStatus.NO_CONTENT);
	}
}
