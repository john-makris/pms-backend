package gr.hua.pms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import gr.hua.pms.payload.request.SignupRequest;
import gr.hua.pms.repository.UserRepository;
import gr.hua.pms.service.UserService;

@SpringBootApplication
public class PresenceManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PresenceManagementSystemApplication.class, args);
	}
	
    @Bean
    public CommandLineRunner firstUserData(UserService userService, UserRepository userRepository) {
        return args -> { 
        	if (userRepository.findAll().isEmpty()) {
	        	SignupRequest adminData = new SignupRequest(
	        				"admin",
	        				"Anargyros",
	        				"Tsadimas",
	        				"pms_admin@hua.gr",
	        				null,
	        				"1234567890",
	        				"1234567890",
	        				null,
	        				null,
	        				null
	        			);
	           userService.createUser(adminData);
        	}
        };
    }

}
