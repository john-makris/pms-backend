package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.CourseSchedule;
import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.Lecture;
import gr.hua.pms.model.LectureType;
import gr.hua.pms.payload.request.LectureRequest;
import gr.hua.pms.payload.response.LectureResponse;
import gr.hua.pms.repository.LectureRepository;

@Service
public class LectureServiceImpl implements LectureService {

	@Autowired
	LectureRepository lectureRepository;
	
	@Autowired
	CourseScheduleService courseScheduleService;
	
	@Override
	public Map<String, Object> findAllSortedPaginated(String filter, int page, int size,
			String[] sort) {

		List<Order> orders = createOrders(sort);

		List<Lecture> lectures = new ArrayList<Lecture>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Lecture> pageLectures = null;

		pageLectures = lectureRepository.searchByFilterSortedPaginated(filter, pagingSort);
		
		lectures = pageLectures.getContent();

		if(lectures.isEmpty()) {
			return null;
		}
		
		List<LectureResponse> lecturesResponse = createLecturesResponse(lectures);
		
		Map<String, Object> response = new HashMap<>();
		response.put("lectures", lecturesResponse);
		response.put("currentPage", pageLectures.getNumber());
		response.put("totalItems", pageLectures.getTotalElements());
		response.put("totalPages", pageLectures.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByCourseScheduleIdSortedPaginated(Long id, String filter, int page, int size,
			String[] sort) {

		List<Order> orders = createOrders(sort);

		List<Lecture> lectures = new ArrayList<Lecture>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Lecture> pageLectures = null;

		pageLectures = lectureRepository.searchByCourseScheduleAndFilterSortedPaginated(id, filter, pagingSort);
		
		lectures = pageLectures.getContent();

		if(lectures.isEmpty()) {
			return null;
		}
		
		List<LectureResponse> lecturesResponse = createLecturesResponse(lectures);
		
		Map<String, Object> response = new HashMap<>();
		response.put("lectures", lecturesResponse);
		response.put("currentPage", pageLectures.getNumber());
		response.put("totalItems", pageLectures.getTotalElements());
		response.put("totalPages", pageLectures.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByDepartmentSortedPaginated(Long departmentId, String filter, int page, int size,
			String[] sort) {

		List<Order> orders = createOrders(sort);

		List<Lecture> lectures = new ArrayList<Lecture>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Lecture> pageLectures = null;

		pageLectures = lectureRepository.searchByDepartmentAndFilterSortedPaginated(departmentId, filter, pagingSort);
		
		lectures = pageLectures.getContent();

		if(lectures.isEmpty()) {
			return null;
		}
		
		List<LectureResponse> lecturesResponse = createLecturesResponse(lectures);
		
		Map<String, Object> response = new HashMap<>();
		response.put("lectures", lecturesResponse);
		response.put("currentPage", pageLectures.getNumber());
		response.put("totalItems", pageLectures.getTotalElements());
		response.put("totalPages", pageLectures.getTotalPages());

		return response;
	}

	@Override
	public Map<String, Object> findAllByDepartmentAndCourseScheduleIdSortedPaginated(Long departmentId,
			Long courseScheduleId, String filter, int page, int size, String[] sort) {

		List<Order> orders = createOrders(sort);

		List<Lecture> lectures = new ArrayList<Lecture>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Lecture> pageLectures = null;

		pageLectures = lectureRepository.searchByCourseSchedulePerDepartmentAndFilterSortedPaginated(departmentId, courseScheduleId, filter, pagingSort);
		
		lectures = pageLectures.getContent();

		if(lectures.isEmpty()) {
			return null;
		}
		
		List<LectureResponse> lecturesResponse = createLecturesResponse(lectures);
		
		Map<String, Object> response = new HashMap<>();
		response.put("lectures", lecturesResponse);
		response.put("currentPage", pageLectures.getNumber());
		response.put("totalItems", pageLectures.getTotalElements());
		response.put("totalPages", pageLectures.getTotalPages());

		return response;
	}
	
	@Override
	public Map<String, Object> findAllByCourseScheduleIdPerTypeSortedPaginated(
			Long courseScheduleId, ELectureType name, String filter, int page, int size, String[] sort) {
		List<Order> orders = createOrders(sort);

		List<Lecture> lectures = new ArrayList<Lecture>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Lecture> pageLectures = null;

		pageLectures = lectureRepository.searchByCourseSchedulePerTypeWithFilterSortedPaginated(courseScheduleId, name, filter, pagingSort);
		
		lectures = pageLectures.getContent();

		if(lectures.isEmpty()) {
			return null;
		}
		
		List<LectureResponse> lecturesResponse = createLecturesResponse(lectures);
		
		Map<String, Object> response = new HashMap<>();
		response.put("lectures", lecturesResponse);
		response.put("currentPage", pageLectures.getNumber());
		response.put("totalItems", pageLectures.getTotalElements());
		response.put("totalPages", pageLectures.getTotalPages());

		return response;
	}
	
	@Override
	public List<Lecture> findAll(String[] sort) throws IllegalArgumentException {
		try {
			return lectureRepository.findAll(Sort.by(createOrders(sort)));
		}catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public LectureResponse findById(Long id) throws IllegalArgumentException {
		Lecture lecture = lectureRepository.checkOwnerShipByLectureId(id);
		if (lecture == null) {
			throw new BadRequestDataException("You don't have view privilege for this lecture, since you are not the owner");
		}
		return createLectureResponse(lecture);
	}

	@Override
	public Lecture save(LectureRequest lectureRequestData) throws IllegalArgumentException {
		if ((lectureRepository.checkOwnershipByCourseScheduleId(lectureRequestData.getCourseSchedule().getId())) == null) {
			// i bazw unauthorized exception kai bgainei eksw apo tin efarmogi moy //////////////////////////////////////////////////////////////////////
			throw new BadRequestDataException("You cannot have the privilege to save, since you are not the owner of the "
					+lectureRequestData.getCourseSchedule().getCourse().getName()+" schedule");
		}
		
		Lecture _lecture = new Lecture();
		CourseSchedule courseSchedule = lectureRequestData.getCourseSchedule();
		LectureType lectureType = lectureRequestData.getLectureType();
		String nameIdentifier = createSimpleNameIdentifier(lectureType.getName(), lectureRequestData.getIdentifierSuffix());
		
		_lecture.setCourseSchedule(courseSchedule);
		_lecture.setLectureType(lectureType);
		_lecture.setTitle(lectureRequestData.getTitle());
		//_lecture.setNameIdentifier(createNameIdentifier(lectureType.getName(), courseSchedule.getId()));
		
		_lecture.setNameIdentifier(nameIdentifier);
		
		if (!lectureRepository.searchByCourseScheduleIdAndNameIdentifier(courseSchedule.getId(), nameIdentifier).isEmpty()) {
			throw new BadRequestDataException(nameIdentifier+" for "+courseSchedule.getCourse().getName()+", already exists");
		}
		
		return checkLecturesCapacity(_lecture);
	}
	
	private Lecture checkLecturesCapacity(Lecture lecture) {
		int lecturesNumber = lectureRepository.searchByCourseScheduleIdAndLectureTypeName(lecture.getCourseSchedule().getId(), lecture.getLectureType().getName()).size();
		if (lecture.getLectureType().getName().equals(ELectureType.Theory)) {
			if (lecture.getCourseSchedule().getMaxTheoryLectures() > lecturesNumber) {
				return lectureRepository.save(lecture);
			} else {
				throw new BadRequestDataException("You cannot create more than "+lecture.getCourseSchedule().getMaxTheoryLectures()+" theories");
			}
		} else if (lecture.getLectureType().getName().equals(ELectureType.Lab)) {
			if (lecture.getCourseSchedule().getMaxLabLectures() > lecturesNumber) {
				return lectureRepository.save(lecture);
			} else {
				throw new BadRequestDataException("You cannot create more than "+lecture.getCourseSchedule().getMaxLabLectures()+" labs");
			}
		} else {
			throw new BadRequestDataException("Lecture type could not recognized");
		}

	}

	@Override
	public Lecture update(Long id, LectureRequest lectureRequestData) {
		Lecture _lecture = lectureRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Lecture with id = " + id));
		if ((lectureRepository.checkOwnershipByCourseScheduleId(_lecture.getCourseSchedule().getId())) == null) {
			throw new BadRequestDataException("You don't have the privilege to update, since you are not the owner of the lecture");
		}
		
		LectureType lectureType = lectureRequestData.getLectureType();
		String nameIdentifier = createSimpleNameIdentifier(lectureType.getName(), lectureRequestData.getIdentifierSuffix());
		CourseSchedule courseSchedule = lectureRequestData.getCourseSchedule();

		_lecture.setTitle(lectureRequestData.getTitle());
		_lecture.setLectureType(lectureRequestData.getLectureType());
		_lecture.setCourseSchedule(courseSchedule);
		
		if (!(lectureRepository.searchByCourseScheduleIdAndNameIdentifier(_lecture.getCourseSchedule().getId(), nameIdentifier)).isEmpty() && !(_lecture.getNameIdentifier().equals(nameIdentifier))) {
			System.out.println("Is empty ? (search by name Identifier and Course Schedule): "
					+(lectureRepository.searchByCourseScheduleIdAndNameIdentifier(courseSchedule.getId(), nameIdentifier)).isEmpty());
			System.out.println("Equals to lecture for update name Identifier? "+_lecture.getNameIdentifier().equals(nameIdentifier));
			throw new BadRequestDataException(nameIdentifier+" for "+courseSchedule.getCourse().getName()+", already exists");
		}
		_lecture.setNameIdentifier(nameIdentifier);
		return lectureRepository.save(_lecture);
	}

	@Override
	public void deleteById(Long id) throws IllegalArgumentException {
		Lecture lecture = lectureRepository.findById(id).orElse(null);
		if(lecture!=null) {
			if (lectureRepository.checkOwnerShipByLectureId(id) == null) {
				throw new BadRequestDataException("You cannot delete the lecture, since you are not the owner");
			} else {
				lectureRepository.deleteById(id);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		lectureRepository.deleteAll();
	}

	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
	    System.out.println("CLASS of "+sort[0]+" is: "+sort[0]);

	    if (sort[0].matches("name")) {
	    	sort[0] = "nameIdentifier";
	    }
	    
	    if (sort[0].contains(",")) {
          // will sort more than 2 fields
          // sortOrder="field, direction"
          for (String sortOrder : sort) {
            String[] _sort = sortOrder.split(",");
            orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
          }
        } else {
          // sort=[field, direction]
          orders.add(new Order(getSortDirection(sort[1]), sort[0]));
        }
	        
	  	return orders;
	}
	
	private Sort.Direction getSortDirection(String direction) {
		  if (direction.equals("asc")) {
			  return Sort.Direction.ASC;
		  } else if (direction.equals("desc")) {
			  return Sort.Direction.DESC;
		  }
			  return Sort.Direction.ASC;
	}
	
	/*private String createAutoNameIdentifier(ELectureType name, Long courseScheduleId) {
		List<Lecture> lectures = new ArrayList<>();
		String identifierSuffix = "";
		String identifier = "";
		
		lectures = lectureRepository.searchByCourseScheduleIdAndLectureTypeName(courseScheduleId, name);

		if (lectures.isEmpty()) {
			identifierSuffix = String.valueOf(1);
		} else {
			identifierSuffix = String.valueOf(lectures.size()+1);
		}
		
		if (name.equals(ELectureType.Theory)) {
			identifier = "theory_"+identifierSuffix;
		}
		
		if (name.equals(ELectureType.Lab)) {
			identifier = "lab_"+identifierSuffix;
		}
		
		return identifier;
	}*/
	
	private String createSimpleNameIdentifier(ELectureType name, String identifierSuffix) {
		String identifier = "";
		
		if (name.equals(ELectureType.Theory)) {
			identifier = "theory_"+identifierSuffix;
		}
		
		if (name.equals(ELectureType.Lab)) {
			identifier = "lab_"+identifierSuffix;
		}
		
		return identifier;
	}
	
	@Override
	public List<LectureResponse> createLecturesResponse(List<Lecture> lectures) {
		List<LectureResponse> lecturesResponse = new ArrayList<LectureResponse>();
		
		lectures.forEach(lecture -> {
			LectureResponse lectureResponse = 
					new LectureResponse(
							lecture.getId(), 
							lecture.getTitle(),
							lecture.getNameIdentifier().split("_", lecture.getNameIdentifier().length())[1],
							lecture.getNameIdentifier(),
							lecture.getLectureType(),
							courseScheduleService.createCourseScheduleResponse(lecture.getCourseSchedule()));
			lecturesResponse.add(lectureResponse);
		});
		
		return lecturesResponse;
	}
	
	@Override
	public LectureResponse createLectureResponse(Lecture lecture) {
		return new LectureResponse(
				lecture.getId(), 
				lecture.getTitle(),
				lecture.getNameIdentifier().split("_", lecture.getNameIdentifier().length())[1],
				lecture.getNameIdentifier(),
				lecture.getLectureType(),
				courseScheduleService.createCourseScheduleResponse(lecture.getCourseSchedule()));
	}

}