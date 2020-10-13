/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Booking;
import entity.BookingSlot;
import util.exceptions.DeleteFormInstanceException;
import entity.FormInstance;
import entity.FormTemplate;
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
import util.exceptions.ArchiveFormInstanceException;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.SubmitFormInstanceException;
import util.exceptions.UpdateFormInstanceException;
import ws.datamodel.CreateFormInstanceReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllFormTemplatesRsp;
import ws.datamodel.RetrieveAllServicemanFormInstancesRsp;
import ws.datamodel.UpdateFormInstanceReq;

/**
 * REST Web Service
 *
 * @author sunag
 */
@Path("Form")
public class FormResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;

    private final FormInstanceSessionBeanLocal formInstanceSessionBeanLocal;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    public FormResource() {

        this.sessionBeanLookup = new SessionBeanLookup();
        this.formInstanceSessionBeanLocal = this.sessionBeanLookup.lookupFormInstanceSessionBeanLocal();
        this.formTemplateSessionBeanLocal = this.sessionBeanLookup.lookupFormTemplateSessionBeanLocal();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();

    }

    @Path("retrieveAllFormTemplates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllFormTemplates(@Context HttpHeaders headers) {

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
            List<FormTemplate> formTemplates = formTemplateSessionBeanLocal.retrieveAllPublishedFormTemplates();
            for (FormTemplate ft : formTemplates) {
                ft.setConsultationPurposes(null);
                ft.setFormInstances(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllFormTemplatesRsp(formTemplates)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("retrieveAllServicemanFormInstances")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllServicemanFormInstances(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

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
            List<FormInstance> formInstances = formInstanceSessionBeanLocal.retrieveServicemanFormInstances(Long.parseLong(servicemanId));
            for (FormInstance fi : formInstances) {

                if (fi.getBooking() != null) {
                    BookingSlot emptyBs = new BookingSlot();
                    emptyBs.setSlotId(fi.getBooking().getBookingSlot().getSlotId());

                    Booking emptyBooking = new Booking();
                    emptyBooking.setBookingSlot(emptyBs);

                    fi.setBooking(emptyBooking);
                }

                fi.setServiceman(null);
                fi.getFormTemplateMapping().setFormInstances(null);
                fi.getFormTemplateMapping().setConsultationPurposes(null);
                
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllServicemanFormInstancesRsp(formInstances)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("createFormInstance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createFormInstance(@Context HttpHeaders headers, CreateFormInstanceReq createFormInstanceReq) {

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

            formInstanceSessionBeanLocal.generateFormInstance(createFormInstanceReq.getServicemanId(), createFormInstanceReq.getFormTemplateId());

            return Response.status(Response.Status.OK).build();

        } catch (GenerateFormInstanceException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("updateFormInstanceFieldValues")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateFormInstanceFieldValues(@Context HttpHeaders headers, UpdateFormInstanceReq updateFormInstanceReq) {

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

            formInstanceSessionBeanLocal.updateFormInstanceFieldValues(updateFormInstanceReq.getFormInstance());

            return Response.status(Response.Status.OK).build();

        } catch (UpdateFormInstanceException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("deleteFormInstance")
    @DELETE
    public Response deleteFormInstance(@Context HttpHeaders headers, @QueryParam("formInstanceId") String formInstanceId) {

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

            formInstanceSessionBeanLocal.deleteFormInstance(Long.parseLong(formInstanceId), Boolean.FALSE);

            return Response.status(Response.Status.OK).build();

        } catch (DeleteFormInstanceException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("submitFormInstance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitFormInstance(@Context HttpHeaders headers, UpdateFormInstanceReq updateFormInstanceReq) {

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

            formInstanceSessionBeanLocal.updateFormInstanceFieldValues(updateFormInstanceReq.getFormInstance());
            formInstanceSessionBeanLocal.submitFormInstance(updateFormInstanceReq.getFormInstance().getFormInstanceId());
            return Response.status(Response.Status.OK).build();

        } catch (SubmitFormInstanceException | UpdateFormInstanceException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("archiveFormInstance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response archiveFormInstance(@Context HttpHeaders headers, UpdateFormInstanceReq updateFormInstanceReq) {

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

            formInstanceSessionBeanLocal.archiveFormInstance(updateFormInstanceReq.getFormInstance().getFormInstanceId());

            return Response.status(Response.Status.OK).build();

        } catch (ArchiveFormInstanceException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

}
