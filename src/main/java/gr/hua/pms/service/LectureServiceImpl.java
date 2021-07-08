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

import gr.hua.pms.exception.ResourceNotFoundException;
import gr.hua.pms.model.Lecture;
import gr.hua.pms.repository.LectureRepository;

@Service
public class LectureServiceImpl implements LectureService {

	@Autowired
	LectureRepository lectureRepository;
	
	@Override
	public Map<String, Object> findAllSorted(Boolean status, int page, int size,
			String[] sort) {

		List<Order> orders = createOrders(sort);

		List<Lecture> lectures = new ArrayList<Lecture>();

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Lecture> pageLectures = null;
		
		if(status==null) {
			try {
				pageLectures = lectureRepository.findAll(pagingSort);
			} catch(Exception e) {
				System.out.println("ERROR: "+e);
			}
		} else {
			pageLectures = lectureRepository.findByPresenceStatementStatusContaining(status, pagingSort);
			System.out.println("3 "+pageLectures);
		}
		
		lectures = pageLectures.getContent();

		if(lectures.isEmpty()) {
			return null;
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("lectures", lectures);
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
	public Lecture findById(Long id) throws IllegalArgumentException {
		Lecture lecture = lectureRepository.findById(id).orElse(null);
		return lecture;
	}

	@Override
	public Lecture save(Lecture lecture) throws IllegalArgumentException {
		return lectureRepository.save(lecture);
	}

	@Override
	public Lecture update(Long id, Lecture lecture) {
		Lecture _lecture = lectureRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Lecture with id = " + id));
		
		_lecture.setTitle(lecture.getTitle());
		_lecture.setRoom(lecture.getRoom());
		_lecture.setDuration(lecture.getDuration());
		_lecture.setLectureDate(lecture.getLectureDate());
		_lecture.setLectureType(lecture.getLectureType());
		_lecture.setPresenceList(lecture.getPresenceList());
		_lecture.setPresenceStatementStatus(lecture.getPresenceStatementStatus());
		_lecture.setExcuseAbsencesLimit(lecture.getExcuseAbsencesLimit());
		_lecture.setActiveCourse(lecture.getActiveCourse());
		
		return lectureRepository.save(_lecture);
	}

	@Override
	public void deleteById(Long id) throws IllegalArgumentException {
		Lecture lecture = lectureRepository.findById(id).orElse(null);
		if(lecture!=null) {
			lectureRepository.deleteById(id);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteAll() {
		lectureRepository.deleteAll();
	}

	@Override
	public List<Order> createOrders(String[] sort) {
	    List<Order> orders = new ArrayList<Order>();
	    
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

}
