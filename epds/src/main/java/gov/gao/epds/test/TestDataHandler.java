package gov.gao.epds.test;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.GC_Track_Service_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.filters.CustomPreAuthenticationFilter;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.utils.ClientInfo;
import gov.gao.epds.utils.SpringApplicationContext;

@Controller
public class TestDataHandler {
    private final static Logger logger = LoggerFactory
            .getLogger(TestDataHandler.class);
	
}
