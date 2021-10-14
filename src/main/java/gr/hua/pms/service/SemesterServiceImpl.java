package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.model.ESemester;
import gr.hua.pms.model.Semester;
import gr.hua.pms.repository.SemesterRepository;

@Service
@Transactional
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;
	
	@Override
	public Semester findSemesterBySemesterNumber(int semesterNumber) {
		return semesterRepository.findBySemesterNumber(semesterNumber)
				.orElseThrow(() -> new RuntimeException("Error: Semester not found"));
	}
	
	@Override
	public List<Semester> findAll() {
		try {
			List<Semester> semesterList = new ArrayList<Semester>();
			semesterList = semesterRepository.findAll();
			if (semesterList.isEmpty()) {
				semesterList = createSemesters();
			}
			return semesterList;
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public List<Semester> createSemesters() {
		List<Semester> semesterList = new ArrayList<Semester>();
		semesterList.add(new Semester("First", ESemester.FIRST.getSemesterNumber()));
		semesterList.add(new Semester("Second", ESemester.SECOND.getSemesterNumber()));
		semesterList.add(new Semester("Third", ESemester.THIRD.getSemesterNumber()));
		semesterList.add(new Semester("Fourth", ESemester.FOURTH.getSemesterNumber()));
		semesterList.add(new Semester("Fifth", ESemester.FIFTH.getSemesterNumber()));
		semesterList.add(new Semester("Sixth", ESemester.SIXTH.getSemesterNumber()));
		semesterList.add(new Semester("Seventh", ESemester.SEVENTH.getSemesterNumber()));
		semesterList.add(new Semester("Eighth", ESemester.EIGHTH.getSemesterNumber()));
		semesterRepository.saveAll(semesterList);
		return semesterList;
	}

}
