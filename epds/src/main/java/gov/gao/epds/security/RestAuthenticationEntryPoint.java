package gov.gao.epds.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Since we make Ajax call from angularjs app.
 * We will send json response to the UI and it will redirect to the login page with the error
 */
@Component
public final class RestAuthenticationEntryPoint implements
		AuthenticationEntryPoint {

	@Override
	public void commence(final HttpServletRequest request,
			final HttpServletResponse response,
			final AuthenticationException authException) throws IOException {
		
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");

		ObjectMapper om = new ObjectMapper();
		ModelMap map = new ModelMap();
		String url  = request.getServletPath();
		if (url.equals("/")){
			map.addAttribute("isBaseUrlAccess",true);	
		}
		
		map.addAttribute("error",
				(String) request.getAttribute("authenticationFailed"));
		map.addAttribute("redirect", "/login");
		String jsonResponse = om.writeValueAsString(map);
		PrintWriter out = response.getWriter();
		out.write(jsonResponse);
		out.flush();
	}

}
