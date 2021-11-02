package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.LectureType;
import gr.hua.pms.repository.LectureTypeRepository;

@Service
@Transactional
public class LectureTypeServiceImpl implements LectureTypeService {

	@Autowired
	LectureTypeRepository lectureTypeRepository;
	
	@Override
	public List<LectureType> findAll() {
		try {
			List<LectureType> lectureTypesList = new ArrayList<LectureType>();
			lectureTypesList = lectureTypeRepository.findAll();
			if (lectureTypesList.isEmpty()) {
				lectureTypesList = createLectureTypes();
			}
			return lectureTypesList;
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public LectureType findLectureTypebyName(ELectureType name) {
		return lectureTypeRepository.findByName(name).
				orElseThrow(() -> new RuntimeException("Error: Room not found"));
	}

	@Override
	public List<LectureType> createLectureTypes() {
		List<LectureType> lectureTypesList = new ArrayList<LectureType>();
		lectureTypesList.add(new LectureType(ELectureType.Theory));
		lectureTypesList.add(new LectureType(ELectureType.Lab));
		lectureTypeRepository.saveAll(lectureTypesList);
		return lectureTypesList;
	}

}
