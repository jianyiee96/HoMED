/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.ConsultationPurpose;
import entity.MedicalCentre;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllConsultationPurposesRsp;
import ws.datamodel.RetrieveAllMedicalCentreRsp;

@Path("MedicalCentre")
public class MedicalCentreResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    public MedicalCentreResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.medicalCentreSessionBeanLocal = this.sessionBeanLookup.lookupMedicalCentreSessionBeanLocal();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
    }

    @Path("retrieveAllMedicalCentres")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllMedicalCentres(@Context HttpHeaders headers) {

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
            List<MedicalCentre> medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();
            for (MedicalCentre mc : medicalCentres) {
                mc.setMedicalStaffList(null);
                mc.setBookingSlots(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllMedicalCentreRsp(medicalCentres)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }
}
