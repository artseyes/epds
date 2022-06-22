package gov.gao.epds.auth.utils;

import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.persistence.entity.User_info;

public class LoginUtil {
	public static User_info_dto getUserInfoDtoForLoginSuccess(
			User_info user_info) {
		User_info_dto user_info_dto = new User_info_dto();

		user_info_dto.setUser_id(user_info.getUser_id());
		user_info_dto.setAccount_status_id(user_info.getAccount_status_id());
		user_info_dto.setAuth_role_id(user_info.getRole_id());

		return user_info_dto;
	}
	
	
	
}
