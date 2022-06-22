package gov.gao.epds.gctrack;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import gov.gao.epds.rest.auth.services.EPDSAuthResourceProvider;

@ApplicationPath("/rest")
public class EPDSRestApp extends Application{
	
	private Set<Object> singletons = new HashSet<Object>();
    public EPDSRestApp() {
        singletons.add(new GCTrackService());
        singletons.add(new EPDSAuthResourceProvider());
        singletons.add(new SecurityInterceptor());
    }
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
