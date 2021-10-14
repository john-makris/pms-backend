package gr.hua.pms.service;

import java.util.List;

import gr.hua.pms.model.Semester;

public interface SemesterService {
	public List<Semester> findAll();

	public Semester findSemesterBySemesterNumber(int semesterNumber);
	
	public List<Semester> createSemesters();
}