package gr.hua.pms.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.exception.WrongFileTypeException;
import gr.hua.pms.helper.CSVHelper;
import gr.hua.pms.helper.ExcelHelper;
import gr.hua.pms.model.User;
import gr.hua.pms.utils.StudentRegisterData;
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

	@Override
	public List<User> find(MultipartFile file) {
		List<User> users = new ArrayList<User>();
		
		try {
		    if (CSVHelper.hasCSVFormat(file)) {
				List<StudentRegisterData> studentRegisterDataList = CSVHelper.csvToStudentRegisterData(file.getInputStream());

				studentRegisterDataList.forEach(studentRegisterData -> {
					users.add(userService.findByAm(studentRegisterData.getAm()));
				});
				
				return users;
				
		    } else if (ExcelHelper.hasExcelFormat(file)) {
			    List<StudentRegisterData> studentRegisterDataList = ExcelHelper.excelToStudentRegisterData(file.getInputStream());

				studentRegisterDataList.forEach(studentRegisterData -> {
					users.add(userService.findByAm(studentRegisterData.getAm()));
				});
				
				return users;

		    } else {
		    	throw new WrongFileTypeException("File type "+file.getContentType()+" is not the corresponding");
		    }
		} catch (IOException e) {
			throw new RuntimeException("Fail to find csv/excel data: " + e.getMessage());
		}
	}

}
