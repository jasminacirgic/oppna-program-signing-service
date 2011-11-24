package se.vgregion.security.sign;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author Patrik Bergström
 */
@Path("/")
@Produces("application/json")
public interface IRestSignController {

    @GET
    @Path("/solveTicket/{serviceId}")
    String solveTicket(@PathParam("serviceId") String serviceId);

}
