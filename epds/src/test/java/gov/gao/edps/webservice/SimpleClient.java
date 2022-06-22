package gov.gao.edps.webservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

public interface SimpleClient {
	@GET
	@Path("print")
	@Produces("application/json")
	String print();

	@PUT
	@Path("basic")
	@Consumes("text/plain")
	void putBasic(String body);

	@GET
	@Path("queryParam")
	@Produces("text/plain")
	String getQueryParam(@QueryParam("param") String param);

	@GET
	@Path("matrixParam")
	@Produces("text/plain")
	String getMatrixParam(@MatrixParam("param") String param);

	@GET
	@Path("uriParam/{param}")
	@Produces("text/plain")
	int getUriParam(@PathParam("param") int param);
}
