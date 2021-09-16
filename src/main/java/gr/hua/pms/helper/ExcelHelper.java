package gr.hua.pms.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.exception.BadRequestDataException;
import gr.hua.pms.utils.UserFileData;

public class ExcelHelper {
	  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	  static String[] HEADERs = { 
			  					  "AM",
			  					  "First Name",
			  					  "Last Name",
								  "Username", 
								  "Email", 
								  "Password",
								  "Department"
								 };
	  
	  //When it used below returns null
	  static String SHEET = "Users";

	  public static boolean hasExcelFormat(MultipartFile file) {

	    if (!TYPE.equals(file.getContentType())) {
	      return false;
	    }

	    return true;
	  }
	  
	  public static List<UserFileData> excelToStudents(InputStream is) {
	    try {
	      Workbook workbook = new XSSFWorkbook(is);
	      System.out.println("Workbook: "+ workbook.getSheet(SHEET));
	      // The line below returns null
	      // Sheet sheet = workbook.getSheet(SHEET);
	      Sheet sheet = workbook.getSheetAt(0);
	      System.out.println("Sheet: "+sheet);
	      Iterator<Row> rows = sheet.iterator();

	      List<UserFileData> usersFileData = new ArrayList<UserFileData>();

	      int rowNumber = 0;
	      while (rows.hasNext()) {
	        Row currentRow = rows.next();
	        int maxNumOfCells = currentRow.getLastCellNum();

	        // skip header
	        if (rowNumber == 0) {
	            for( int cellCounter = 0; 
	            		cellCounter < maxNumOfCells; cellCounter ++){
            		if (!HEADERs[cellCounter].equals(currentRow.getCell(cellCounter).getStringCellValue())) {
	            		System.out.println("HEADERs "+HEADERs[cellCounter]);
	            		System.out.println("Value: "+currentRow.getCell(cellCounter).getStringCellValue().trim());

					    workbook.close();
			    		throw new BadRequestDataException("Excel file Headers: "+currentRow.getCell(cellCounter).getStringCellValue().trim()+""
			    				+ ", in column "+cellCounter+" don't matches with the corresponding ("+HEADERs[cellCounter]+")");
            		}
	            	
	            }

	          rowNumber++;
	          continue;
	        }

            for( int cellCounter = 0; 
            		cellCounter < maxNumOfCells; cellCounter ++){
            	if (currentRow.getCell(cellCounter) == null) {
            		currentRow.createCell(cellCounter);
            	} else {
            		currentRow.getCell(cellCounter);
            	}
            }
	        
	        Iterator<Cell> cellsInRow = currentRow.iterator();

	        UserFileData userFileData = new UserFileData();
	        System.out.println("cellsInRow: " +cellsInRow);
	        try {
		        int cellIdx = 0;
		        while (cellsInRow.hasNext()) {
		          Cell currentCell = cellsInRow.next();
	
		          switch (cellIdx) {
	
		          case 0:
		        	  userFileData.setAm(String.valueOf((long) currentCell.getNumericCellValue()));
		        	  System.out.println("AM: "+userFileData.getAm());
		            break;
		            
		          case 1:
		        	  userFileData.setFirstname(currentCell.getStringCellValue());
		        	  System.out.println("First name: "+userFileData.getFirstname());
		            break;
		            
		          case 2:
		        	  userFileData.setLastname(currentCell.getStringCellValue());
		        	  System.out.println("Last name: "+userFileData.getLastname());
		            break;
		            
		          case 3:
		        	  userFileData.setUsername(currentCell.getStringCellValue());
		            break;
	
		          case 4:
		        	  userFileData.setEmail(currentCell.getStringCellValue());
		            break;
	
		          case 5:
		        	  userFileData.setPassword(currentCell.getStringCellValue());
		        	  System.out.println("Mystery password: "+currentCell.getStringCellValue());
		        	  //user.setPublished(currentCell.getBooleanCellValue());
		            break;
		            
		          case 6:
		        	  userFileData.setDepartmentName(currentCell.getStringCellValue());
		            break;
	
		          default:
		            break;
		          }
	
	        	  userFileData.setStatus(true);
	        	  userFileData.setFileRowNumber(rowNumber + 1);
		          cellIdx++;
		        }
	        	
		    } catch(Exception e) {
			    workbook.close();
	    		throw new BadRequestDataException("The content of the excel file is inappropriate");
	    	}


	        usersFileData.add(userFileData);
	      }

	      workbook.close();

	      return usersFileData;
	    } catch (IOException e) {
	      throw new RuntimeException("Fail to parse Excel file: " + e.getMessage());
	    }
	  }
}
