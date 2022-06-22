package gov.gao.epds.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import gov.treas.fms.services.tcsonline_3_0.TCSServiceFault_Exception;

/**
 * Need to come back and properly handle other exceptions
 * @author MHussaini
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({NullPointerException.class,IOException.class,FileNotFoundException.class,InvocationTargetException.class})
    public ModelMap handleAll(Exception e) {
    	
    	String uniqueErrorId = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
    	
    	
        log.error("Unhandled exception occurred Message={}, ErrorId={}", e.getMessage(), uniqueErrorId);
        e.printStackTrace();
        ModelMap model = new ModelMap();
        
        model.addAttribute("errorId", uniqueErrorId);
        
        model.addAttribute("error", "There was problem processing your request");
        
        
        return model;
    }
   
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({SQLException.class})
    public ModelMap handleSQLExceptions(Exception e) {
    	
    	String uniqueErrorId = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
    	
    	
        log.error("Unhandled exception occurred Message={}, ErrorId={}", e.getMessage(), uniqueErrorId);
        e.printStackTrace();
        ModelMap model = new ModelMap();
        
        model.addAttribute("errorId", uniqueErrorId);
        
        model.addAttribute("error", "Database error!!");
        
        
        return model;
    }
    
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({TCSServiceFault_Exception.class})
    public ModelMap handleTCSServiceFault_Exception(TCSServiceFault_Exception e) {
    	
    	String uniqueErrorId = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
    	
    	
        log.error("Unhandled exception occurred Message={}, ErrorId={}", e.getMessage(), uniqueErrorId);
        e.printStackTrace();
        ModelMap model = new ModelMap();
        
        
        Map<String, Object> errorMap = new HashMap<String,Object>();
        
		errorMap.put("errorCode", e.getFaultInfo().getReturnCode());
		errorMap.put("errorDetail", e.getFaultInfo().getReturnDetail());
		
       
        model.addAttribute("errorId", uniqueErrorId);
        
        model.addAttribute("error", errorMap);
        
        
        return model;
    }
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({IllegalArgumentException.class, RuntimeException.class})
    public ModelMap handleIllegalArgumentExceptions(Exception e) {
    	
    	String uniqueErrorId = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
    	
        log.error("Unhandled exception occurred Message={}, ErrorId={}", e.getMessage(), uniqueErrorId);
        e.printStackTrace();
        ModelMap model = new ModelMap();
        
        model.addAttribute("errorId", uniqueErrorId);
        
        model.addAttribute("error", "IllegalArgumentException!!! ");
        
        
        return model;
    }
    
    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({IllegalAccessError.class})
    public ModelMap handleIllegalAccessExceptions(Exception e) {
    	
    	String uniqueErrorId = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
    	
        log.error("Unhandled exception occurred Message={}, ErrorId={}", e.getMessage(), uniqueErrorId);
        e.printStackTrace();
        ModelMap model = new ModelMap();
        
        model.addAttribute("errorId", uniqueErrorId);
        
        model.addAttribute("error", "IllegalArgumentException!!! ");
        
        
        return model;
    }
    
    
    @ExceptionHandler({Exception.class})
    public ModelMap handleAllExceptions(Exception e) {
    	
    	String uniqueErrorId = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + RandomStringUtils.randomAlphanumeric(15);
    	
        log.error("Unhandled exception occurred Message={}, ErrorId={}", e.getMessage(), uniqueErrorId);
        e.printStackTrace();
        ModelMap model = new ModelMap();
        
        model.addAttribute("errorId", uniqueErrorId);
        
        model.addAttribute("error", "There was a problem processing your request!! ");
        
        
        return model;
    }

}