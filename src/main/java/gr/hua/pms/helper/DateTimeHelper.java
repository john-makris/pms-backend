package gr.hua.pms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeHelper {

	public static String calcAcademicYear() {
		String academicYear = null;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("The right date format: "+dateTimeFormatter.format(now));  
		int currentMonth = 5;//now.getMonthValue();
		System.out.println("Current month: "+currentMonth);
		if (currentMonth > 2 && currentMonth <= 12 ) {
			academicYear = String.valueOf(now.getYear()+1) + " - " + String.valueOf(now.getYear() + 2);
		} else {
			academicYear = String.valueOf(now.getYear() - 1) + " - " + String.valueOf(now.getYear());
		}
		
		return academicYear;
	}
	
	public static boolean calcCurrentSemester() {
		boolean isWinterSemester = false;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("The right date format: "+dateTimeFormatter.format(now));  
		int currentMonth = now.getMonthValue();
		System.out.println("Current month: "+currentMonth);
		if (currentMonth > 2 && currentMonth < 10 ) {
			isWinterSemester = false;
		} else {
			isWinterSemester = true;
		}
		
		return isWinterSemester;
	}
}
