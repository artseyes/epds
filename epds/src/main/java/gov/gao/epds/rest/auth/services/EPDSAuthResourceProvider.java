package gov.gao.epds.rest.auth.services;

import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.utils.SpringApplicationContext;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/auth-service")
public class EPDSAuthResourceProvider {
	
	@RolesAllowed("EPDS_AUTH")
	@GET
	@Path("/get-agency-poc-user_infos/{userId}")
	@Produces("application/json")
	public Object getAgencyPOCUserInfos(@PathParam("userId") Integer userId) { // NO_UCD (unused code)
		try {
			UserInfoService userInfoService = (UserInfoService) SpringApplicationContext
					.getBean("userInfoService");
			
			
			return userInfoService.getListOfAllAgencyPOCEmailAddressesByUserId(String.valueOf(userId));

		} catch (Exception e) {
			return "N/A";
		}

	}

	@RolesAllowed("EPDS_AUTH")
	@GET
	@Path("/test")
	@Produces("application/json")
	public Object getAgencyPOCEmails() {
		try {
			UserInfoService userInfoService = (UserInfoService) SpringApplicationContext
					.getBean("userInfoService");
			userInfoService.getAgencyPOCUserInfos(1);

		} catch (Exception e) {
			return "N/A";
		}

		return "test success";
	}

	@RolesAllowed("EPDS_AUTH")
	@GET
	@Path("/get-gao-admin-user_infos")
	@Produces("application/json")
	public Object getGaoAdminUserInfos() {
		try {
			UserInfoService userInfoService = (UserInfoService) SpringApplicationContext
					.getBean("userInfoService");
			return userInfoService.getGAOAdminUserInfos();

		} catch (Exception e) {
			return "N/A";
		}
	}
}
