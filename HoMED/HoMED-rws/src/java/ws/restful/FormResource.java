/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author sunag
 */
@Path("form")
public class FormResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of FormResource
     */
    public FormResource() {
    }

    /**
     * Retrieves representation of an instance of ws.restful.FormResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of FormResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
