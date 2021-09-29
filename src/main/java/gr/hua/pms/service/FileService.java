package gr.hua.pms.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import gr.hua.pms.model.User;

public interface FileService {
	
	public void save(MultipartFile file);
	
	public List<User> find(MultipartFile file);
}