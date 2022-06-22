package gov.gao.epds.enums;

import java.util.HashMap;
import java.util.Map;

/*
1	PROTESTER
2	AGENCY
3	GAO
*/

public enum UserAuthRoles {
    PROTESTER(1, "PROTESTER"),
    AGENCY(2, "INTERVENOR"),
    GAO(3, "GAO ATTORNEY");

    private static Map<Integer, UserAuthRoles> statusCodeMap = new HashMap<Integer, UserAuthRoles>();
    private static Map<String, UserAuthRoles> statusNameMap = new HashMap<String, UserAuthRoles>();

    static{
        for(UserAuthRoles statusTypes : UserAuthRoles.values()){
            statusCodeMap.put(statusTypes.getCode(), statusTypes);
            statusNameMap.put(statusTypes.getName(), statusTypes);

        }
    }

    private Integer code;
    private String name;

    private UserAuthRoles(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserAuthRoles getByCode(Integer code){
        return statusCodeMap.get(code);
    }

    public static UserAuthRoles getByName(String name){
        return statusNameMap.get(name);
    }

    public static Map<Integer, UserAuthRoles> getStatusCodeMap() {
        return statusCodeMap;
    }

    public static Map<String, UserAuthRoles> getStatusNameMap() {
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
