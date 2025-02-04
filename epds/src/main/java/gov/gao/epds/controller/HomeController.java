package gov.gao.epds.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gao.epds.dto.BrowserLogs;
import gov.gao.epds.dto.DashboardDto;
import gov.gao.epds.logging.ui.LoggingUtil;
import gov.gao.epds.persistence.entity.Invited_User;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.service.CaseDocketSheetService;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.HomeService;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;


@Controller
public class HomeController { // NO_UCD (unused code)
	
	private final static Logger logger = LoggerFactory
			.getLogger(HomeController.class);
	@Autowired
	private HomeService homeService;
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private DashboardService dashboardService;

	
	@Autowired
	private CaseDocketSheetService caseDocketSheetService;
	
	@Autowired
	private ProtestInfoService protestInfoService;
	
	
	
	/**
	 * Retrieves different dashboards based on different user role.
	 * 
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @param protestTableType
	 * @throws Exception
	 */
	@RequestMapping(value = "/dashboard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getDashboard(
			HttpServletRequest request,
			ModelMap map,
			RedirectAttributes redirecmap,
			@RequestParam(value = "protestTableType", required = false) String protestTableType, DashboardDto dashboardDto)
			throws Exception {
		
		int totalRecords = dashboardDto.getEndLimit() - dashboardDto.getStartLimit();
		if (totalRecords > 150){
			throw new IllegalArgumentException("Start Limit and End limit cannot be greater than 150");
		}
		
		EpdsSession.removeAttribute(request, "protestInfo");
		List<String> caseStatusList = new ArrayList<String>();
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		
		
		EpdsSession.setAttribute(request, "userprofileId", user_Info.getUser_Id());
		String user_role = (String) EpdsSession.getAttribute(request,
				"user_Role");
		Integer groupId = 0;
		
		
		protestTableType = homeService.getProtestTableType(protestTableType,
				user_Info);
		
		
		
		if ((user_Info.getRole_id().equals(8) || user_Info.getRole_id().equals(7)) 
				&& dashboardDto.isFullReloadReq()){
			map.addAttribute("gaoUserList", caseDocketSheetService.getListOfGAOUserConsistOfAttorneyAndSupervisor());
			groupId = homeService.getGroupId(user_Info);
			
			user_Info.setGroup_No(groupId);
		}else{
			dashboardDto.setFullReloadReq(false);
		}
		
		caseStatusList.add("CLOSED");
		caseStatusList.add("OPEN");
		// caseStatusList.add("COMPLETE");
		// if (user_Info.getRole_id().equals(7) || user_Info.getRole_id().equals(6) || user_Info.getRole_id().equals(3)){
		if (user_Info.getRole_id().equals(7)  || user_Info.getRole_id().equals(3)){
			caseStatusList.add("READY TO COMPLETE");
		}
		map.addAttribute("caseStatusList", caseStatusList);
		List<Protest_Info> protest_InfoList = homeService.getProtestInfoList(
				user_Info, protestTableType, user_role,dashboardDto);
		
		
		
		if (protest_InfoList != null) {
			map.addAttribute("unjoinedProtestInfoList", protest_InfoList);
			//map.addAttribute("mainProtestInfoList", protest_InfoList);
			Map<String, Protest_Info> protest_InfoMap = homeService
					.getProtestInfoMap(protest_InfoList);
			
			 
			EpdsSession.setAttribute(request, "protest_InfoMap",
					protest_InfoMap);
			
			EpdsSession.setAttribute(request, "protest_InfoList",
					protest_InfoList);
			

			protest_InfoList = homeService.assignParentAndChildRelation(
					protest_InfoMap, protest_InfoList, user_role);

			if(protestTableType.equalsIgnoreCase("allAgencyCases")){
				homeService.populateAgencyRepsForProtestInfoList(protest_InfoList);
			}

			
			
			map.addAttribute("displayProtestTable", true);
			map.addAttribute("protest_Info_List", protest_InfoList);
			if (groupId != 0) {
				map.addAttribute("group_No", groupId);
			}
		} else {
			map.addAttribute("role", user_role.trim());
			map.addAttribute("displayProtestTable", false);
		}
		
		
		map.addAttribute("role", user_role.trim());
		map.addAttribute("userProfileInfo", user_Info);
		map.addAttribute("numberOfDaysLeftToExpirePwd", 
				EpdsSession.getAttribute(request,"numberOfDaysLeftToExpirePwd"));
		
		return map;
		
	}

	/**
	 * Redirect to User Profile View Page
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/user-profile-view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getUserProfileView(HttpServletRequest request, ModelMap map,
			RedirectAttributes redirecmap) throws Exception {
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		user_Info = userInfoService.getUser_InfoByEmail(user_Info.getEmail());
		map.addAttribute("user_Info", user_Info);
		map.addAttribute("user_Role",
				(String) EpdsSession.getAttribute(request, "user_Role"));
		
		
		return map;
		
	}

	/**
	 * When User Log Out The User Id is removed from the EDS Session Map Object .
	 * Also the access token is remove from the browser.
	 * @param model
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	public void logout(Model model, HttpServletRequest request,
			HttpServletResponse response) {
		
		EpdsSession.endUserSession(request);

		for (Cookie cookie : request.getCookies()) {
			cookie.setValue("");
			cookie.setMaxAge(0);
			cookie.setPath("/epds");
			response.addCookie(cookie);
		}
	}

	/**
	 * Retrieves list of secondary rep invites for current logged in User.
	 * @param request
	 * @param map
	 */
	@RequestMapping(value = "/secondary-rep-invites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getInvitation(HttpServletRequest request, ModelMap map) {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");

		String userId = user_Info.getUser_Id();

		List<Invited_User> invited_user_list = homeService
				.getInvitedSecondaryProtester(userId);

		map.addAttribute("invitedUserList", invited_user_list);
		
		return map;
	}

	
	
	/**
	 * This is used to capture browser logs especially javascript errors.  
	 * @param request
	 * @param browserLogs
	 */
	@RequestMapping(value = "/logger", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public void logger(HttpServletRequest request, @RequestBody BrowserLogs browserLogs) {
		
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");

		ObjectMapper om = new ObjectMapper();
		try {
			String jsonMessage = om.writeValueAsString(browserLogs);
			LoggingUtil.saveBrowserLogs(user_Info,jsonMessage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error("Exception occurred when logging browser logs", e);
		}
		
		
	}
}
