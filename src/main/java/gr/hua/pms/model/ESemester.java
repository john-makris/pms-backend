package gr.hua.pms.model;

public enum ESemester {
	FIRST(1),
	SECOND(2),
	THIRD(3),
	FOURTH(4),
	FIFTH(5),
	SIXTH(6),
	SEVENTH(7),
	EIGHTH(8);
	
	private final int semesterNumber;

	ESemester(int semesterNumber) {
		this.semesterNumber = semesterNumber;
	}

	public int getSemesterNumber() {
		return semesterNumber;
	}
}