/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Notification;
import entity.Serviceman;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.ReadAllNotificationsReq;
import ws.datamodel.ReadNotificationReq;
import ws.datamodel.RetrieveAllServicemanNotificationsRsp;
import ws.datamodel.HasUnfetchedServicemanNotificationsRsp2;

/**
 * REST Web Service
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Path("Notification")
public class NotificationResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private final NotificationSessionBeanLocal notificationSessionBeanLocal;

    /**
     * Creates a new instance of NotificationResource
     */
    public NotificationResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
        this.notificationSessionBeanLocal = this.sessionBeanLookup.lookupNotificationSessionBeanLocal();
    }

    @Path("retrieveAllServicemanNotifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllServicemanNotifications(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

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
            List<Notification> notifications = notificationSessionBeanLocal.retrieveAllNotificationsByServicemanId(Long.parseLong(servicemanId), true);
            notifications.forEach(n -> n.setServiceman(null));
            return Response.status(Response.Status.OK).entity(new RetrieveAllServicemanNotificationsRsp(notifications)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("hasUnfetchedNotifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hasUnfetchedServicemanNotifications(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

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
            List<Notification> unfetchedNotifications = notificationSessionBeanLocal.retrieveAllUnfetchedNotificationsByServicemanId(Long.parseLong(servicemanId));
            return Response.status(Response.Status.OK).entity(new HasUnfetchedServicemanNotificationsRsp2(!unfetchedNotifications.isEmpty())).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("readNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readNotification(@Context HttpHeaders headers, ReadNotificationReq readNotificationReq) {
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

            if (readNotificationReq.getNotificationId() != null) {
                notificationSessionBeanLocal.readNotification(Long.parseLong(readNotificationReq.getNotificationId()));

                return Response.status(Response.Status.OK).build();
            } else {
                ErrorRsp errorRsp = new ErrorRsp("Unable to get read notification, invalid ReadNotificationReq.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("readAllNotifications")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAllNotifications(@Context HttpHeaders headers, ReadAllNotificationsReq readAllNotificationsReq) {
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

            if (readAllNotificationsReq.getServicemanId() != null) {
                Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(Long.parseLong(readAllNotificationsReq.getServicemanId()));

                for (Notification n : serviceman.getNotifications()) {
                    if (n.getIsFetched() && !n.getIsRead()) {
                        notificationSessionBeanLocal.readNotification(n.getNotificationId());
                    }
                }

                return Response.status(Response.Status.OK).build();
            } else {
                ErrorRsp errorRsp = new ErrorRsp("Failed to read all notifications, invalid ReadAllNotificationsReq.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("deleteNotification")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNotification(@Context HttpHeaders headers, @QueryParam("notificationId") String notificationId) {
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

            notificationSessionBeanLocal.deleteNotification(Long.parseLong(notificationId));

            return Response.status(Response.Status.OK).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("deleteAllNotification")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllNotification(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {
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

            Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(Long.parseLong(servicemanId));

            for (Notification n : serviceman.getNotifications()) {
                if (n.getIsFetched()) {
                    notificationSessionBeanLocal.deleteNotification(n.getNotificationId());
                }
            }

            return Response.status(Response.Status.OK).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

}
