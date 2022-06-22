package gov.gao.epds.gctrack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.gao.epds.rest.auth.services.AuthenticationController;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PropertyFileEncrypter;

/**
 * This interceptor verify the access permissions for a user based on passowrd provided in request
 */
@Provider
public class SecurityInterceptor implements ContainerRequestFilter {
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401,
			new Headers<Object>());;
	private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403,
			new Headers<Object>());;
	private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500,
			new Headers<Object>());;
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);	
	
    @Context
    private HttpServletRequest request;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		Method method = methodInvoker.getMethod();
		logger.info("Resource access requestUrl={} method={}", requestContext.getUriInfo().getPath(true),method);
		// Access allowed for all
		if (!method.isAnnotationPresent(PermitAll.class)) {

			// Access denied for all
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}

			// Get request headers
			final MultivaluedMap<String, String> headers = requestContext.getHeaders();

			// Fetch authorization header
			final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

			// If no authorization information present; block access
			if (authorization == null || authorization.isEmpty()) {
				requestContext.abortWith(ACCESS_DENIED);
				return;
			}

			// Get password
			final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

			// Verify user access
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

				// Is this a GCTrack request?
				if (rolesSet.contains("GCTRACK")) {

					if (!authenticateGCTRackRequest(encodedUserPassword)){
						requestContext.abortWith(ACCESS_DENIED);
						return;
					}
				}

				// Is this a EPDS_AUTH request?
				if (rolesSet.contains("EPDS_AUTH")) {
					String epds_auth_header = PropertyFileEncrypter
							.decrypt(GlobalParams.prop.getProperty("epds_auth_header_pass"));

					if (!epds_auth_header.equals(encodedUserPassword)) {
						requestContext.abortWith(ACCESS_DENIED);
						return;
					}

				}

			}
		}
	}

	private boolean authenticateGCTRackRequest(String encodedUserPassword) {
		boolean isAllowed = false;
		try {
			isAllowed = AuthenticationService.authenticate(encodedUserPassword);
			
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("GCTRACK", "GCTRACK", new ArrayList<GrantedAuthority>()));

			/*if (isAllowed) {
				isAllowed = AuthenticationService.authenticateIpAddress(request);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return isAllowed;
	}

}
