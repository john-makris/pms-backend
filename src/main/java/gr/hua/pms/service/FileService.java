package gr.hua.pms.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	
	public void save(MultipartFile file);
}