package gov.epds.filter;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.epds.filter.utils.HTTPCacheHeader;
import gov.epds.filter.utils.HttpUtils;


public class LoginFilter implements Filter {
	
	
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	 HttpServletRequest request = (HttpServletRequest) req;
         String servletPath = request.getServletPath();
         ServletContext context = request.getServletContext();
         RequestDispatcher requestDispatcher =  null;
         String decodedIsUserLoggedIn, decodedUserRoleId;
         
        String userLoggedIn =  HttpUtils.extractCookieValue(request, "ls.userLoggedIn");
        String roleId =  HttpUtils.extractCookieValue(request, "ls.id");
        
        
        try {
        	userLoggedIn = new String(Base64.getUrlDecoder().decode(String.valueOf(userLoggedIn).getBytes("UTF-8")), "utf-8");
        	roleId = new String(Base64.getUrlDecoder().decode(String.valueOf(roleId).getBytes("UTF-8")), "utf-8");
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        
        HttpServletResponse httpServletResponse = (HttpServletResponse) res;

        
        if (!servletPath.contains("bootstrap") 
        		&& !servletPath.toLowerCase(Locale.ENGLISH).contains("font-awesome")){
        	 // set cache directives
            httpServletResponse.setHeader(HTTPCacheHeader.CACHE_CONTROL.getName(), "no-store, no-cache, must-revalidate, private");
            httpServletResponse.setDateHeader(HTTPCacheHeader.EXPIRES.getName(), 0L);
         
            // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
//            httpServletResponse.addHeader(HTTPCacheHeader.CACHE_CONTROL.getName(), "post-check=0, pre-check=0");
            // Set standard HTTP/1.0 no-cache header.
            httpServletResponse.setHeader(HTTPCacheHeader.PRAGMA.getName(), "no-cache");
            
            //httpServletResponse.setContentType("application/json;charset=ISO-8859-1");
            //httpServletResponse.setCharacterEncoding("UTF-8");;
        }
       
    	
        
        
        if (servletPath.startsWith("/scripts")
    		|| servletPath.startsWith("/img")
    		|| servletPath.startsWith("/styles")
    		|| servletPath.startsWith("/views")
            || servletPath.startsWith("/favicon.ico")){
    		
    		String clientIP = HttpUtils.getClientIpAddr(request);
            
    		/*
    		&& servletPath.startsWith("/scripts/app/admin/case-docket-sheet/gc-case-docket.js")
    		&& !servletPath.startsWith("/scripts/app/core")
    		&& !servletPath.startsWith("/scripts/app/sidebar")
    		&& !servletPath.startsWith("/scripts/app/registration")
    		&& !servletPath.startsWith("/scripts/app/error")
    		//&& !servletPath.startsWith("/scripts/app/account-update")
    		&& !servletPath.startsWith("/scripts/app/action-messages")*/
            
    		/*
    		 * Amer: come back and add the rules for all the necessary directories that we want to hide 
    		*/
    		/*if (servletPath.startsWith("/scripts/app/dashboard") 
    				&& !servletPath.startsWith("/scripts/app/dashboard/dashboard-utils")
    				&& !"7".equalsIgnoreCase(roleId)
    				&& !"Y".equalsIgnoreCase(userLoggedIn)){
            	
            			HttpUtils.printClientInfo(request, "");
            			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not allowed to access this page!!!");
            	 return;
            }*/

    		
    		
    		if ( servletPath.startsWith("/scripts/app/admin")
            		&& !servletPath.startsWith("/scripts/app/admin/case-docket-sheet/gc-case-docket")
    				&& (!"Y".equalsIgnoreCase(userLoggedIn))){

            			HttpUtils.printClientInfo(request, "");
            			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not allowed to access this page!!!");
            	 return;
            }
    		
    		
          
            chain.doFilter(req, res);
    	}else {
    
    		requestDispatcher = context.getRequestDispatcher("/index.html");
    		try {
                requestDispatcher.forward(req, res);
            } catch (ServletException e) {
            } catch (IOException e) {
            }
    	}
        
    }
    @Override
    public void destroy() {
    	
    }
}
