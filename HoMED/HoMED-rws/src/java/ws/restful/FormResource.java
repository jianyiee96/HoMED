/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import entity.FormTemplate;
import entity.Serviceman;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllFormTemplatesRsp;
import ws.datamodel.ServicemanLoginRsp;

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

        System.out.println("GET FORM TEMPLATE API CAllED!");

        try {
            List<FormTemplate> formTemplates = formTemplateSessionBeanLocal.retrieveAllPublishedFormTemplates();
            for(FormTemplate ft : formTemplates) {
                ft.setConsultationPurposes(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllFormTemplatesRsp(formTemplates)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }
}
