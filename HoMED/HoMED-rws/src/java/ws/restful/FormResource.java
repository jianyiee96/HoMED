/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import entity.FormTemplate;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.GenerateFormInstanceException;
import ws.datamodel.CreateFormInstanceReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllFormTemplatesRsp;

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

    public FormResource() {

        this.sessionBeanLookup = new SessionBeanLookup();
        this.formInstanceSessionBeanLocal = this.sessionBeanLookup.lookupFormInstanceSessionBeanLocal();
        this.formTemplateSessionBeanLocal = this.sessionBeanLookup.lookupFormTemplateSessionBeanLocal();

    }
    
    @Path("retrieveAllFormTemplates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllFormTemplates() {

        try {
            List<FormTemplate> formTemplates = formTemplateSessionBeanLocal.retrieveAllPublishedFormTemplates();
            for(FormTemplate ft : formTemplates) {
                ft.setConsultationPurposes(null);
                ft.setFormInstances(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllFormTemplatesRsp(formTemplates)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }
    
    @Path("createFormInstance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createFormInstance(CreateFormInstanceReq createFormInstanceReq) {
        
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
}
