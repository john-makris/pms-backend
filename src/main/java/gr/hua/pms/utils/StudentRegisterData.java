package gr.hua.pms.utils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class StudentRegisterData {
	
    @NotBlank(message = "AM required")
	@Size(min = 5, max = 8,
	message = "AM must be between 2 and 8 integers")
	@Pattern(regexp ="[0-9]+", message = "AM must contain only integer numbers")
    private String am;
    
    @NotNull
    private int fileRowNumber;
    

	public String getAm() {
		return am;
	}

	public void setAm(String am) {
		this.am = am;
	}

	public int getFileRowNumber() {
		return fileRowNumber;
	}

	public void setFileRowNumber(int fileRowNumber) {
		this.fileRowNumber = fileRowNumber;
	}
    
}