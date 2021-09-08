package gr.hua.pms.payload.response;

import java.util.ArrayList;
import java.util.List;

import gr.hua.pms.utils.Violation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse {

	private List<Violation> violations = new ArrayList<>();

	public List<Violation> getViolations() {
		return violations;
	}

	public void setViolations(List<Violation> violations) {
		this.violations = violations;
	}
	  
}