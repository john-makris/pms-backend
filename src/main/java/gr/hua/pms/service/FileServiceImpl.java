package gr.hua.pms.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.exception.WrongFileTypeException;
import gr.hua.pms.helper.CSVHelper;
import gr.hua.pms.helper.ExcelHelper;
import gr.hua.pms.utils.UserFileData;

@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	UserService userService;
	
	@Override
	public void save(MultipartFile file) {
		try {
		    if (CSVHelper.hasCSVFormat(file)) {
				List<UserFileData> usersFileData = CSVHelper.csvToStudents(file.getInputStream());
				//
			    userService.createStudentsFromFile(usersFileData);
		    } else if (ExcelHelper.hasExcelFormat(file)) {
			    List<UserFileData> usersFileData = ExcelHelper.excelToStudents(file.getInputStream());
			    //
			    userService.createStudentsFromFile(usersFileData);
		    } else {
		    	throw new WrongFileTypeException("File type "+file.getContentType()+" is not the corresponding");
		    }
		} catch (IOException e) {
			throw new RuntimeException("Fail to store csv/excel data: " + e.getMessage());
		}
	}

}
