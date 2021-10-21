package gr.hua.pms.service;

import java.util.List;

import gr.hua.pms.model.ELectureType;
import gr.hua.pms.model.LectureType;

public interface LectureTypeService {
	public List<LectureType> findAll();

	public LectureType findLectureTypebyName(ELectureType name);
	
	public List<LectureType> createLectureTypes();
}