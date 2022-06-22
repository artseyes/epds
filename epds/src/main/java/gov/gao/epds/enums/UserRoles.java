package gov.gao.epds.enums;

import java.util.HashMap;
import java.util.Map;

/*
1	PROTESTER
2	INTERVENOR
3	GAO ATTORNEY
4	SECONDARY PROTESTER
7	GAO ADMIN
5	AGENCY ADMIN
6	AGENCY ATTORNEY
8	GAO SUPERVISOR
9	SECONDARY INTERVENOR
*/

public enum UserRoles {
	PROTESTER(1, "PROTESTER"),
	INTERVENOR(2, "INTERVENOR"),
	GAO_ATTORNEY(3, "GAO ATTORNEY"),
	SECONDARY_PROTESTER(4, "SECONDARY PROTESTER"),
	AGENCY_ADMIN(5, "AGENCY ADMIN"),
	AGENCY_ATTORNEY(6, "AGENCY ATTORNEY"),
    GAO_ADMIN(7, "GAO ADMIN"),
    GAO_SUPERVISOR(8, "GAO SUPERVISOR"),
    SECONDARY_INTERVENOR(9, "SECONDARY INTERVENOR");

	private static Map<Integer,UserRoles> statusCodeMap = new HashMap<Integer,UserRoles>();
	private static Map<String,UserRoles> statusNameMap = new HashMap<String,UserRoles>();
	
	static{
		for(UserRoles statusTypes : UserRoles.values()){
			statusCodeMap.put(statusTypes.getCode(), statusTypes);
			statusNameMap.put(statusTypes.getName(), statusTypes);
			
		}
	}

	private Integer code;
	private String name;
	
	private UserRoles(Integer code,String name) {
		this.code = code;	
		this.name = name;
	}
	
	public static UserRoles getByCode(Integer code){
		return statusCodeMap.get(code);
	}
	
	public static UserRoles getByName(String name){
		return statusNameMap.get(name);
	}

	public static Map<Integer, UserRoles> getStatusCodeMap() {
		return statusCodeMap;
	}

	public static Map<String, UserRoles> getStatusNameMap() {
		return statusNameMap;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
