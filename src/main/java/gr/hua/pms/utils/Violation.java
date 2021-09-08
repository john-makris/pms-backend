package gr.hua.pms.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Violation {

	private final String fieldName;
	private final String message;
	  
	public String getFieldName() {
		return fieldName;
	}
	public String getMessage() {
		return message;
	}
	  
}