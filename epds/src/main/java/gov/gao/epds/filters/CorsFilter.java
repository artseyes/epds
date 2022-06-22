/*package gov.gao.epds.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

*//**
 * A filter to facilitate CORS handling.
 * To disable this (e.g. while testing or in non-browser apps),
 * in your application.properties, don't provide
 * the <code>lemon.cors.allowedOrigins</code> property.
 * 
 *//*


@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // needs to come first 

public class CorsFilter extends OncePerRequestFilter {

	private final Log log = LogFactory.getLog(CorsFilter.class);


	@Override protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain) throws
			ServletException, IOException {

		log.debug("Inside CORS Filter");

		Cors cors = new Cors();

		String origin = request.getHeader("Origin");

		response.setHeader("Access-Control-Allow-Origin",StringUtils.join(cors.getAllowedOrigins(), ","));

		// allowed methods 
		response.setHeader("Access-Control-Allow-Methods",StringUtils.join(cors.getAllowedMethods(), ","));

		// allow headers 
		response.setHeader("Access-Control-Allow-Headers",StringUtils.join(cors.getAllowedHeaders(), ","));

		
		response.setHeader("Access-Control-Expose-Headers",
				StringUtils.join(cors.getExposedHeaders(), ","));

		// max age 
		response.setHeader("Access-Control-Max-Age",Long.toString(cors.getMaxAge()));

		response.setHeader("Access-Control-Allow-Credentials", "true");

		// Don't let OPTIONs pass. 
		// Otherwise certain things like Spring Security
		// don't behave properly sometimes. 
		// E.g., the SwitchUserFilter doesn't work. 

		if (!request.getMethod().equals("OPTIONS")) 
			filterChain.doFilter(request,response); 

	} 
	
}
*/