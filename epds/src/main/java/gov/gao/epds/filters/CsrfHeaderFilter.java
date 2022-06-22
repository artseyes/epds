package gov.gao.epds.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * Come back to this...
 * 
 * Cross-Site Request Forgery (CSRF) is a type of attack that occurs when a
 * malicious web site, email, blog, instant message, or program causes a user's
 * web browser to perform an unwanted action on a trusted site for which the
 * user is currently authenticated. The impact of a successful CSRF attack is
 * limited to the capabilities exposed by the vulnerable application. For
 * example, this attack could result in a transfer of funds, changing a
 * password, or purchasing an item in the user's context. In effect, CSRF
 * attacks are used by an attacker to make a target system perform a function
 * via the target's browser without knowledge of the target user, at least until
 * the unauthorized transaction has been committed.
 * 
 * Impacts of successful CSRF exploits vary greatly based on the privileges of
 * each victim. When targeting a normal user, a successful CSRF attack can
 * compromise end-user data and their associated functions. If the targeted end
 * user is an administrator account, a CSRF attack can compromise the entire web
 * application. Sites that are more likely to be attacked by CSRF are community
 * websites (social networking, email) or sites that have high dollar value
 * accounts associated with them (banks, stock brokerages, bill pay services).
 * Utilizing social engineering, an attacker can embed malicious HTML or
 * JavaScript code into an email or website to request a specific 'task URL'.
 * The task then executes with or without the user's knowledge, either directly
 * or by utilizing a Cross-Site Scripting flaw (ex: Samy MySpace Worm).
 * 
 * For more information on CSRF, please see the OWASP Cross-Site Request Forgery
 * (CSRF) page.
 * 
 * Angular packages the CSRF token approach, making it simpler for us to
 * implement. For every request that your Angular application makes of your
 * server, the Angular $http service will do these things automatically:
 * 
 * Look for a cookie named XSRF-TOKEN on the current domain. If that cookie is
 * found, it reads the value and adds it to the request as the X-XSRF-TOKEN
 * header.
 * 
 * Thus the client-side implementation is handled for you, automatically! But
 * this does leave the server side pieces in your hands. You will need to do the
 * following parts:
 * 
 * During login: create the CSRF token (with a random, un-guessable string), and
 * associate it with the user session. You will need to send it on the login
 * response as the XSRF-TOKEN cookie. Assert that all incoming requests to your
 * API have the X-XSRF-TOKEN header, and that the value of the header is the
 * token that is associated with the user's session.
 * 
 * @author MHussaini
 *
 */
@Component
public class CsrfHeaderFilter extends OncePerRequestFilter {

	protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
	protected static final String RESPONSE_HEADER_NAME = "X-XSRF-TOKEN";
	protected static final String RESPONSE_PARAM_NAME = "X-XSRF-PARAM";
	protected static final String RESPONSE_TOKEN_NAME = "X-XSRF-TOKEN";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
		String token;

		if (csrf != null) {
			token = csrf.getToken();
			if (cookie  == null || (null != token && !token.equals(cookie.getValue()))) {
				cookie = new Cookie("XSRF-TOKEN", token);

				if(request.isSecure()){
					cookie.setSecure(true);
				}

				cookie.setPath("/epds");

				response.addCookie(cookie);
			}
			
			/*
			 * we also need to set the csrf token in request header because CORS
			 * cannot read cookies even if they are http only
			 */

			response.setHeader(RESPONSE_HEADER_NAME, csrf.getHeaderName());
			response.setHeader(RESPONSE_PARAM_NAME, csrf.getParameterName());
			response.setHeader(RESPONSE_TOKEN_NAME, csrf.getToken());

		}
		filterChain.doFilter(request, response);
	}

	private boolean isAuthenticating(HttpServletRequest servletRequest) {
		return servletRequest.getRequestURI().equals("/login");
	}
}
