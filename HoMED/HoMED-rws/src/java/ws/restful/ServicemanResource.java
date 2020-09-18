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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import util.exceptions.ServicemanInvalidPasswordException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateServicemanException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.ServicemanChangePassReq;
import ws.datamodel.ServicemanLoginReq;
import ws.datamodel.ServicemanLoginRsp;
import ws.datamodel.ServicemanResetPassReq;
import ws.datamodel.ServicemanUpdateReq;
import ws.datamodel.ServicemanUpdateRsp;

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

    @Path("login")
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

    @Path("changePassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response servicemanChangePassword(ServicemanChangePassReq servicemanChangePassReq) {

        if (servicemanChangePassReq != null) {

            try {
                servicemanSessionBeanLocal.changePassword(servicemanChangePassReq.getNric(), servicemanChangePassReq.getOldPassword(), servicemanChangePassReq.getNewPassword());

                return Response.status(Response.Status.OK).build();
            } catch (ServicemanInvalidPasswordException | ServicemanNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid change password request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("updateServiceman")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateServiceman(ServicemanUpdateReq servicemanUpdateReq) {
        if (servicemanUpdateReq != null) {

            try {
                System.out.println(servicemanUpdateReq.getServiceman().getNric());
                Serviceman updatedServiceman = servicemanSessionBeanLocal.updateServiceman(servicemanUpdateReq.getServiceman());

                return Response.status(Response.Status.OK).entity(new ServicemanUpdateRsp(updatedServiceman)).build();
            } catch (UpdateServicemanException ex) {
                System.out.println(ex);
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                System.out.println(ex);
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {

            ErrorRsp errorRsp = new ErrorRsp("Invalid update serviceman request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("resetPassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(ServicemanResetPassReq servicemanResetPassReq) {
        if (servicemanResetPassReq != null) {

            try {

                servicemanSessionBeanLocal.resetServicemanPassword(servicemanResetPassReq.getNric(), servicemanResetPassReq.getEmail());
                return Response.status(Response.Status.OK).build();

            } catch (ResetServicemanPasswordException ex) {

                System.out.println(ex);
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();

            } catch (Exception ex) {
                System.out.println(ex);
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } else {

            ErrorRsp errorRsp = new ErrorRsp("Invalid reset password request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }
}
