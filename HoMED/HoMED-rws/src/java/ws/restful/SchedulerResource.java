/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.ServicemanSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import java.util.Date;
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
import util.exceptions.ScheduleBookingSlotException;
import ws.datamodel.CancelBookingReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.QueryBookingSlotsReq;
import ws.datamodel.ScheduleBookingReq;

@Path("Scheduler")
public class SchedulerResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private final SlotSessionBeanLocal slotSessionBeanLocal;
    
    public SchedulerResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
        this.slotSessionBeanLocal = this.sessionBeanLookup.lookupSlotPurposeSessionBeanLocal();
    }

    @Path("queryBookingSlots")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBookingSlots(@Context HttpHeaders headers, QueryBookingSlotsReq queryBookingSlotsReq) {

//        try {
//            String token = headers.getRequestHeader("Token").get(0);
//            String id = headers.getRequestHeader("Id").get(0);
//
//            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
//                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
//                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
//            }
//
//        } catch (Exception ex) {
//            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
//            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
//        }

        ErrorRsp errorRsp = new ErrorRsp("Not implemented");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();

    }

    @Path("scheduleBooking")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleBooking(@Context HttpHeaders headers, ScheduleBookingReq scheduleBookingReq) {

//        try {
//            String token = headers.getRequestHeader("Token").get(0);
//            String id = headers.getRequestHeader("Id").get(0);
//
//            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
//                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
//                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
//            }
//
//        } catch (Exception ex) {
//            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
//            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
//        }
        ErrorRsp errorRsp = new ErrorRsp("Not implemented");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();

    }

    @Path("cancelBooking")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleBooking(@Context HttpHeaders headers, CancelBookingReq cancelBookingReq) {

//        try {
//            String token = headers.getRequestHeader("Token").get(0);
//            String id = headers.getRequestHeader("Id").get(0);
//
//            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
//                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
//                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
//            }
//
//        } catch (Exception ex) {
//            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
//            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
//        }
        ErrorRsp errorRsp = new ErrorRsp("Not implemented");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();

    }

    @Path("retrieveAllServicemanBookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllServicemanBookings(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

//        try {
//            String token = headers.getRequestHeader("Token").get(0);
//            String id = headers.getRequestHeader("Id").get(0);
//
//            if (!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))) {
//                ErrorRsp errorRsp = new ErrorRsp("Invalid JSON Token");
//                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
//            }
//
//        } catch (Exception ex) {
//            ErrorRsp errorRsp = new ErrorRsp("Missing JSON Token");
//            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
//        }
        ErrorRsp errorRsp = new ErrorRsp("Not implemented");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();

    }

}
