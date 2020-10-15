/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.ConsultationPurpose;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllConsultationPurposesRsp;

@Path("Consultation")
public class ConsultationResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    public ConsultationResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.consultationPurposeSessionBeanLocal = this.sessionBeanLookup.lookupConsultationPurposeSessionBeanLocal();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
    }

    @Path("retrieveAllConsultationPurposes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllConsultationPurposes(@Context HttpHeaders headers) {

        try {
            String token = headers.getRequestHeader("Token").get(0);
            String id = headers.getRequestHeader("Id").get(0);

            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            }

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

        try {
            List<ConsultationPurpose> consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
            for (ConsultationPurpose cp : consultationPurposes) {
                cp.setFormTemplates(null);
                cp.setBookings(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllConsultationPurposesRsp(consultationPurposes)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("retrieveConsultationPosition")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveConsultationPosition(@Context HttpHeaders headers, @QueryParam("consultationId") String consultationId) {

        try {
            String token = headers.getRequestHeader("Token").get(0);
            String id = headers.getRequestHeader("Id").get(0);

            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            }

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

        ErrorRsp errorRsp = new ErrorRsp("Not implemented");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
    }

    @Path("retrieveServicemanConsultations")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveServicemanConsultations(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

        try {
            String token = headers.getRequestHeader("Token").get(0);
            String id = headers.getRequestHeader("Id").get(0);

            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            }

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

        ErrorRsp errorRsp = new ErrorRsp("Not implemented");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
    }

}
