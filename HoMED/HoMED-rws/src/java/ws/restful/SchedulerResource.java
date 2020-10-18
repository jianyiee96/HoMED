/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Booking;
import entity.BookingSlot;
import entity.FormInstance;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.CancelBookingException;
import util.exceptions.CreateBookingException;
import ws.datamodel.CancelBookingReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.QueryBookingSlotsReq;
import ws.datamodel.QueryBookingSlotsRsp;
import ws.datamodel.RetrieveBookingsRsp;
import ws.datamodel.ScheduleBookingReq;

@Path("Scheduler")
public class SchedulerResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private final SlotSessionBeanLocal slotSessionBeanLocal;

    private final BookingSessionBeanLocal bookingSessionBeanLocal;

    public SchedulerResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
        this.slotSessionBeanLocal = this.sessionBeanLookup.lookupSlotSessionBeanLocal();
        this.bookingSessionBeanLocal = this.sessionBeanLookup.lookupBookingSessionBeanLocal();
    }

    @Path("queryBookingSlots")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBookingSlots(@Context HttpHeaders headers, QueryBookingSlotsReq queryBookingSlotsReq) {

        System.out.println("Query Date: " + queryBookingSlotsReq.getQueryDate());

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

        List<BookingSlot> bookingSlots = this.slotSessionBeanLocal.retrieveMedicalCentreBookingSlotsByDate(queryBookingSlotsReq.getMedicalCentreId(), queryBookingSlotsReq.getQueryDate());
        for (BookingSlot b : bookingSlots) {
            b.setMedicalCentre(null);
            if (b.getBooking() != null) {
                b.getBooking().setBookingSlot(null);
                b.getBooking().setConsultationPurpose(null);
                b.getBooking().setFormInstances(null);
                b.getBooking().setServiceman(null);
                b.getBooking().setConsultation(null);
            }
        }
        return Response.status(Response.Status.OK).entity(new QueryBookingSlotsRsp(bookingSlots)).build();

    }

    @Path("scheduleBooking")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleBooking(@Context HttpHeaders headers, ScheduleBookingReq scheduleBookingReq) {

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

            bookingSessionBeanLocal.createBooking(scheduleBookingReq.getServicemanId(), scheduleBookingReq.getConsultationPurposeId(), scheduleBookingReq.getBookingSlotId());

            return Response.status(Response.Status.OK).build();

        } catch (CreateBookingException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("cancelBooking")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@Context HttpHeaders headers, CancelBookingReq cancelBookingReq) {

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
            bookingSessionBeanLocal.cancelBooking(cancelBookingReq.getBookingId());
            return Response.status(Response.Status.OK).build();

        } catch (CancelBookingException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("retrieveAllServicemanBookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllServicemanBookings(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

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

        List<Booking> bookings = bookingSessionBeanLocal.retrieveServicemanBookings(Long.parseLong(servicemanId));

        for (Booking b : bookings) {
            b.setServiceman(null);
            b.getConsultationPurpose().setBookings(null);
            b.getConsultationPurpose().setFormTemplates(null);
            b.getBookingSlot().setBooking(null);
            b.getBookingSlot().getMedicalCentre().setMedicalStaffList(null);
            b.getBookingSlot().getMedicalCentre().setBookingSlots(null);
            for (FormInstance fi : b.getFormInstances()) {
                if (fi.getSignedBy() != null) {
                    fi.getSignedBy().setSignedFormInstances(null);
                    fi.getSignedBy().setCurrentConsultation(null);
                    fi.getSignedBy().setCompletedConsultations(null);
                    fi.getSignedBy().setMedicalCentreToNull();
                }
                fi.setServiceman(null);
                fi.setBooking(null);
                fi.getFormTemplateMapping().setFormInstances(null);
                fi.getFormTemplateMapping().setConsultationPurposes(null);
            }
            b.setConsultation(null);
        }

        return Response.status(Response.Status.OK).entity(new RetrieveBookingsRsp(bookings)).build();

    }

}
