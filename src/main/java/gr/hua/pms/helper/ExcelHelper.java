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

import gr.hua.pms.utils.UserFileData;

public class ExcelHelper {
	  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	  static String[] HEADERs = { 
			  					  "AM",
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

	        // skip header
	        if (rowNumber == 0) {
	          rowNumber++;
	          continue;
	        }

	        int maxNumOfCells = currentRow.getLastCellNum();
            for( int cellCounter = 0
                    ; cellCounter < maxNumOfCells
                    ; cellCounter ++){
            	if (currentRow.getCell(cellCounter) == null) {
            		currentRow.createCell(cellCounter);
            	} else {
            		currentRow.getCell(cellCounter);
            	}
            }
	        
	        Iterator<Cell> cellsInRow = currentRow.iterator();

	        UserFileData userFileData = new UserFileData();
	        System.out.println("cellsInRow: " +cellsInRow);
	        int cellIdx = 0;
	        while (cellsInRow.hasNext()) {
	          Cell currentCell = cellsInRow.next();

	          switch (cellIdx) {
	          /*case 0:
	        	  user.setId((long) currentCell.getNumericCellValue());
	            break;*/

	          case 0:
	        	  userFileData.setAm(currentCell.getStringCellValue());
	        	  System.out.println("AM: "+userFileData.getAm());
	            break;
	            
	          case 1:
	        	  userFileData.setUsername(currentCell.getStringCellValue());
	            break;

	          case 2:
	        	  userFileData.setEmail(currentCell.getStringCellValue());
	            break;

	          case 3:
	        	  userFileData.setPassword(currentCell.getStringCellValue());
	        	  System.out.println("Mystery password: "+currentCell.getStringCellValue());
	        	  //user.setPublished(currentCell.getBooleanCellValue());
	            break;
	            
	          case 4:
	        	  userFileData.setDepartmentName(currentCell.getStringCellValue());
	            break;
	            
	          /*case 4:
	        	  user.setStatus(currentCell.getBooleanCellValue());
	            break;*/

	          default:
	            break;
	          }

        	  userFileData.setStatus(true);
        	  userFileData.setFileRowNumber(rowNumber + 2);
	          cellIdx++;
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
