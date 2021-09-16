package gr.hua.pms.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.utils.UserFileData;

public class CSVHelper {

	  public static String TYPE = "text/csv";
	  static String[] HEADERs = { 
			  					  "AM",
			  					  "First Name",
			  					  "Last Name",
								  "Username", 
								  "Email", 
								  "Password",
								  "Department"
								 };

	  public static boolean hasCSVFormat(MultipartFile file) {
	
	    if (!TYPE.equals(file.getContentType())) {
	      return false;
	    }

	    return true;
	  }
	  
	  public static List<UserFileData> csvToStudents(InputStream is) {
	    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        CSVParser csvParser = new CSVParser(fileReader,
	            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

	      List<UserFileData> usersFileData = new ArrayList<UserFileData>();

	      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

	      try {
		      int index = 0;
		      for (CSVRecord csvRecord : csvRecords) {
		    	  
			    	  UserFileData userFileData = new UserFileData(
					          csvRecord.get("AM"),
				              csvRecord.get("First name"),
				              csvRecord.get("Last name"),
				              csvRecord.get("Username"),
				              csvRecord.get("Email"),
				              csvRecord.get("Password"),
				              csvRecord.get("Department"),
				              true,
				              index + 2
				            ); 

		    	  usersFileData.add(userFileData);
		    	  index ++;
		      }
	      } catch(Exception e) {
    		  throw new BadRequestDataException("The content of the csv file is inappropriate");
    	  }

	      return usersFileData;
	    } catch (IOException e) {
	      throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
	    }
	  }
}
