package gov.gao.epds.filters;


import gov.gao.epds.enums.HTTPCacheHeader;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(-1)
public class CacheFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        HttpServletResponse httpServletResponse = (HttpServletResponse) res;
        httpServletResponse.setHeader(HTTPCacheHeader.CACHE_CONTROL.getName(), "no-store, private");
//        httpServletResponse.setDateHeader(HTTPCacheHeader.EXPIRES.getName(), 0L);

        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
//        httpServletResponse.addHeader(HTTPCacheHeader.CACHE_CONTROL.getName(), "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
//        httpServletResponse.setHeader(HTTPCacheHeader.PRAGMA.getName(), "no-cache");

        chain.doFilter(req, res);

    }
    @Override
    public void destroy() {
    	
    }
}
