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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.ActivateServicemanException;
import util.exceptions.ChangeServicemanPasswordException;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateServicemanException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.ServicemanActivateAccountReq;
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

        if (servicemanLoginReq != null) {

            try {
                Serviceman serviceman = servicemanSessionBeanLocal.servicemanLogin(servicemanLoginReq.getEmail(), servicemanLoginReq.getPassword());
                serviceman.setFormInstances(null);
                return Response.status(Response.Status.OK).entity(new ServicemanLoginRsp(serviceman)).build();
            } catch (ServicemanInvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid login request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("changePassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response servicemanChangePassword(@Context HttpHeaders headers, ServicemanChangePassReq servicemanChangePassReq) {

        String token = headers.getRequestHeader("Token").get(0);
        String id = headers.getRequestHeader("Id").get(0);
        
        if(!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))){
            ErrorRsp errorRsp = new ErrorRsp("Invalid Token");
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        }
        
        if (servicemanChangePassReq != null) {

            try {
                servicemanSessionBeanLocal.changeServicemanPassword(servicemanChangePassReq.getEmail(), servicemanChangePassReq.getOldPassword(), servicemanChangePassReq.getNewPassword(), servicemanChangePassReq.getConfirmNewPassword());

                return Response.status(Response.Status.OK).build();
            } catch (ChangeServicemanPasswordException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid change password request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("activateAccount")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response servicemanActivateAccount(ServicemanActivateAccountReq servicemanActivateAccountReq) {

        if (servicemanActivateAccountReq != null) {

            try {
                servicemanSessionBeanLocal.activateServiceman(servicemanActivateAccountReq.getEmail(), servicemanActivateAccountReq.getNewPassword(), servicemanActivateAccountReq.getConfirmNewPassword());

                return Response.status(Response.Status.OK).build();
            } catch (ActivateServicemanException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid activate account request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }
    
    @Path("updateServiceman")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateServiceman(@Context HttpHeaders headers, ServicemanUpdateReq servicemanUpdateReq) {

        String token = headers.getRequestHeader("Token").get(0);
        String id = headers.getRequestHeader("Id").get(0);
        
        if(!(servicemanSessionBeanLocal.verifyToken(Long.parseLong(id), token))){
            ErrorRsp errorRsp = new ErrorRsp("Invalid Token");
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        }

        if (servicemanUpdateReq != null) {

            try {
                Serviceman updatedServiceman = servicemanSessionBeanLocal.updateServiceman(servicemanUpdateReq.getServiceman());
                updatedServiceman.setFormInstances(null);
                return Response.status(Response.Status.OK).entity(new ServicemanUpdateRsp(updatedServiceman)).build();
            } catch (UpdateServicemanException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
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

                servicemanSessionBeanLocal.resetServicemanPassword(servicemanResetPassReq.getEmail(), servicemanResetPassReq.getPhoneNumber());
                return Response.status(Response.Status.OK).build();

            } catch (ResetServicemanPasswordException ex) {

                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();

            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } else {

            ErrorRsp errorRsp = new ErrorRsp("Invalid reset password request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }
}
