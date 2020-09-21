package ws.restful;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Path("employee")
public class EmployeeResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;
    
    private final EmployeeSessionBeanLocal employeeSessionBeanLocal;
  
    public EmployeeResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.employeeSessionBeanLocal = this.sessionBeanLookup.lookupEmployeeSessionBeanLocal();
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getEmployee(@QueryParam("employeeId") String id) {
        Employee employee = this.employeeSessionBeanLocal.retrieveEmployeeById(Long.parseLong(id));
        
        if(employee != null){ 
            return Response.status(Response.Status.OK).entity("Found TestEntity with id = " + id + " Name: " + employee.getName()).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Unable to find TestEntity with id = " + id).build();
        }   
    }

}