
package gov.gao.epds.auth.rest;

import gov.gao.epds.auth.service.LoginService;
import gov.gao.epds.auth.utils.SpringApplicationContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/*@Path("/auth-service")*/
public class AuthenticationService {

	@GET
	@Path("/authenticate/")
	@Produces("application/json")
	public Object getAuthenticationResponse() {
		System.out.println("test success!!");

		LoginService loginService = (LoginService) SpringApplicationContext
				.getBean("loginService");

		return null;
	}
}
