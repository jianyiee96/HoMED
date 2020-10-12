/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.ConsultationPurpose;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ws.datamodel.CreateFormInstanceReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllConsultationPurposesRsp;
import ws.datamodel.UploadRsp;

@Path("Consultation")
public class ConsultationResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    public ConsultationResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.consultationPurposeSessionBeanLocal = this.sessionBeanLookup.lookupConsultationPurposeSessionBeanLocal();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
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
            List<ConsultationPurpose> consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
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

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@FormDataParam("file") InputStream uploadedFileInputStream,
            @FormDataParam("file") FormDataContentDisposition uploadedFileDetails) {
        
        System.out.println("upload api called. File name: "+ uploadedFileDetails.getFileName());
        return Response.status(Status.OK).build();

//        try {
//            System.err.println("********** FileResource.upload()");
//
////          String outputFilePath = servletContext.getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + uploadedFileDetails.getFileName();
//            String outputFilePath = "C:\\" + uploadedFileDetails.getFileName();
//            File file = new File(outputFilePath);
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//
//            int a;
//            int BUFFER_SIZE = 8192;
//            byte[] buffer = new byte[BUFFER_SIZE];
//
//            while (true) {
//                a = uploadedFileInputStream.read(buffer);
//
//                if (a < 0) {
//                    break;
//                }
//
//                fileOutputStream.write(buffer, 0, a);
//                fileOutputStream.flush();
//            }
//
//            fileOutputStream.close();
//            uploadedFileInputStream.close();
//
//            return Response.status(Status.OK).entity(new UploadRsp("ok")).build();
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//
//            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new UploadRsp("file processing error")).build();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//
//            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new UploadRsp("file processing error")).build();
//        }
    }

    @POST
    @Path("test")
    public Response test() {
        System.out.println("Api called!");
        return Response.status(Status.OK).build();

    }

}
