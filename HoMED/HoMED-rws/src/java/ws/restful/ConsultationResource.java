/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Booking;
import entity.BookingSlot;
import entity.Consultation;
import entity.ConsultationPurpose;
import entity.FormInstance;
import entity.MedicalCentre;
import entity.MedicalOfficer;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllConsultationPurposesRsp;
import ws.datamodel.RetrieveConsultationsRsp;
import ws.datamodel.RetrieveQueuePositionRsp;

@Path("Consultation")
public class ConsultationResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    private final ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    public ConsultationResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.consultationPurposeSessionBeanLocal = this.sessionBeanLookup.lookupConsultationPurposeSessionBeanLocal();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
        this.consultationSessionBeanLocal = this.sessionBeanLookup.lookupConsultationSessionBeanLocal();
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
            List<ConsultationPurpose> consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllActiveConsultationPurposes();
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

    @Path("retrieveConsultationQueuePosition")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveConsultationQueuePosition(@Context HttpHeaders headers, @QueryParam("consultationId") String consultationId) {

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

            int position = consultationSessionBeanLocal.retrieveConsultationQueuePosition(Long.parseLong(consultationId));

            return Response.status(Response.Status.OK).entity(new RetrieveQueuePositionRsp(position)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
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

        try {
            List<Consultation> consultations = consultationSessionBeanLocal.retrieveAllServicemanConsultations(Long.parseLong(servicemanId));
            
            for (Consultation c : consultations) {
                if (c.getMedicalBoardCase() != null) {
                    c.getMedicalBoardCase().setConsultation(null);
                }
                Booking b = c.getBooking();

                BookingSlot bs = b.getBookingSlot();
                bs.setBooking(null);

                MedicalCentre mc = bs.getMedicalCentre();
                mc.setBookingSlots(null);
                mc.setMedicalStaffList(null);

                b.setConsultation(null);
                b.setConsultationPurpose(new ConsultationPurpose(b.getConsultationPurpose().getConsultationPurposeName()));
                b.setServiceman(null);
                List<FormInstance> formInstances = b.getFormInstances();
                for (FormInstance fi : formInstances) {
                    fi.setBooking(null);
                    fi.setServiceman(null);
                    fi.getFormTemplateMapping().setFormInstances(null);
                    fi.getFormTemplateMapping().setConsultationPurposes(null);
                    if (fi.getSignedBy() != null) {
                        MedicalOfficer mo = new MedicalOfficer();
                        mo.setName(fi.getSignedBy().getName());
                        mo.setSalt(null);
                        mo.setAddress(null);
                        mo.setCompletedConsultations(null);
                        mo.setIsActivated(null);
                        fi.setSignedBy(mo);
                    }
                }

                if (c.getMedicalOfficer() != null) {
                    MedicalOfficer mo = new MedicalOfficer();
                    mo.setName(c.getMedicalOfficer().getName());
                    mo.setSalt(null);
                    mo.setAddress(null);
                    mo.setCompletedConsultations(null);
                    mo.setIsActivated(null);
                    c.setMedicalOfficer(mo);
                }

            }
            
            return Response.status(Response.Status.OK).entity(new RetrieveConsultationsRsp(consultations)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

}
