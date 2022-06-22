package gov.gao.epds.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.ui.ModelMap;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private String errorMessage;

	public CustomAccessDeniedHandler() {
	}

	public CustomAccessDeniedHandler(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) 
                throws IOException, ServletException {
		ModelMap map = new ModelMap();
		
		map.addAttribute("response", response);
		map.addAttribute("redirectPage", "/login");
		map.addAttribute("exception", accessDeniedException );
		

	}


}
