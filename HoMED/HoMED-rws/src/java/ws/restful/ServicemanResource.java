/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Serviceman;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.ServicemanLoginReq;
import ws.datamodel.ServicemanLoginRsp;

/**
 * REST Web Service
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Path("Serviceman")
public class ServicemanResource {

    @Context
    private UriInfo context;
    
    private final SessionBeanLookup sessionBeanLookup;
    
    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    /**
     * Creates a new instance of ServicemanResource
     */
    public ServicemanResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
    }

    @Path("servicemanLogin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response servicemanLogin(ServicemanLoginReq servicemanLoginReq) {
        
        try {
            Serviceman serviceman = servicemanSessionBeanLocal.servicemanLogin(servicemanLoginReq.getNric(), servicemanLoginReq.getPassword());
            System.out.println("=-=-=-=-=-=-=-= Serviceman " + serviceman.getEmail() + " login remotely via web service");

            return Response.status(Response.Status.OK).entity(new ServicemanLoginRsp(serviceman)).build();
        } catch (ServicemanInvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
