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
import gr.hua.pms.utils.StudentRegisterData;
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
	  
	  public static List<StudentRegisterData> csvToStudentRegisterData(InputStream is) {
	    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        CSVParser csvParser = new CSVParser(fileReader,
	            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

		      List<StudentRegisterData> studentRegisterDataList = new ArrayList<StudentRegisterData>();

        	  if (!HEADERs[0].equals(csvParser.getHeaderNames().get(0))) {
            	  System.out.println("HEADERs "+HEADERs[0]);
            	  System.out.println("Value: "+csvParser.getHeaderNames().get(0));


            	  throw new BadRequestDataException("Csv file Headers: "+csvParser.getHeaderNames().get(0)+""
  	    				+ ", in column "+0+" don't matches with the corresponding ("+HEADERs[0]+")");
        	  }
        	  
        	  if (csvParser.getHeaderNames().size() > 1) {
            	  System.out.println("***********HEADER NAMES: "+csvParser.getHeaderNames().size());
            	  throw new BadRequestDataException("Csv file Headers, must contain only the "+HEADERs[0]+" field");
        	  }
	          
		      Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	
		      try {
			      int index = 0;
			      for (CSVRecord csvRecord : csvRecords) {
			    	  
			    	  StudentRegisterData studentRegisterData = new StudentRegisterData(
					          csvRecord.get("AM"),
				              index + 2
				            ); 
	
			    	  studentRegisterDataList.add(studentRegisterData);
			    	  index ++;
			      }
		      } catch(Exception e) {
	    		  throw new BadRequestDataException("The content of the csv file is inappropriate");
	    	  }

	      return studentRegisterDataList;
	    } catch (IOException e) {
	      throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
	    }
	  }
	  
	  public static List<UserFileData> csvToStudents(InputStream is) {
	    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        CSVParser csvParser = new CSVParser(fileReader,
	            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

	      List<UserFileData> usersFileData = new ArrayList<UserFileData>();
	      
          for( int cellCounter = 0; 
          		cellCounter < csvParser.getHeaderNames().size(); cellCounter ++){
      		if (!HEADERs[cellCounter].equals(csvParser.getHeaderNames().get(cellCounter))) {
          		System.out.println("HEADERs "+HEADERs[cellCounter]);
          		System.out.println("Value: "+csvParser.getHeaderNames().get(cellCounter));

	    		throw new BadRequestDataException("Csv file Headers: "+csvParser.getHeaderNames().get(cellCounter)+""
	    				+ ", in column "+cellCounter+" don't matches with the corresponding ("+HEADERs[cellCounter]+")");
      		}
          	
          }
          
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
