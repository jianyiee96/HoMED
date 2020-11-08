/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import ejb.session.stateless.ConditionStatusSessionBeanLocal;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.ConditionStatus;
import entity.MedicalBoardCase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllServicemanMedicalBoardCasesRsp;
import ws.datamodel.RetrieveAllServicemanStatusesRsp;
import ws.wrapper.ConditionStatusWrapper;
import ws.wrapper.MedicalBoardCaseWrapper;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("MedicalBoard")
public class MedicalBoardResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private final ConditionStatusSessionBeanLocal conditionStatusSessionBeanLocal;

    private final MedicalBoardCaseSessionBeanLocal medicalBoardCaseSessionBeanLocal;

    /**
     * Creates a new instance of MedicalBoardResource
     */
    public MedicalBoardResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.servicemanSessionBeanLocal = this.sessionBeanLookup.lookupServicemanSessionBeanLocal();
        this.conditionStatusSessionBeanLocal = this.sessionBeanLookup.lookupConditionStatusSessionBeanLocal();
        this.medicalBoardCaseSessionBeanLocal = this.sessionBeanLookup.lookupMedicalBoardCaseSessionBeanLocal();
    }

    @Path("retrieveAllServicemanStatuses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllServicemanStatuses(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

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
            List<ConditionStatus> conditionStatuses = conditionStatusSessionBeanLocal.retrieveConditionStatusByServiceman(Long.parseLong(servicemanId));
            List<ConditionStatusWrapper> conditionStatusWrappers = new ArrayList<>();

            for (ConditionStatus cs : conditionStatuses) {
                Date startDate = cs.getMedicalBoardCase().getMedicalBoardSlot().getActualEndDateTime();
                Long medicalBoardCaseId = cs.getMedicalBoardCase().getMedicalBoardCaseId();
                cs.setMedicalBoardCase(null);
                cs.setServiceman(null);
                conditionStatusWrappers.add(new ConditionStatusWrapper(cs, medicalBoardCaseId, startDate));
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllServicemanStatusesRsp(conditionStatusWrappers)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("retrieveAllServicemanMedicalBoardCases")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllServicemanMedicalBoardCases(@Context HttpHeaders headers, @QueryParam("servicemanId") String servicemanId) {

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
            List<MedicalBoardCase> medicalBoardCases = medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesByServiceman(Long.parseLong(servicemanId));

            List<MedicalBoardCaseWrapper> medicalBoardCaseWrappers = new ArrayList<>();

            for (MedicalBoardCase mbc : medicalBoardCases) {

                Date scheduledStartDate = mbc.getMedicalBoardSlot().getStartDateTime();
                Date scheduledEndDate = mbc.getMedicalBoardSlot().getEndDateTime();
                String chairman = mbc.getMedicalBoardSlot().getChairman().getName();
                List<ConditionStatusWrapper> conditionStatusWrappers = new ArrayList<>();

                for (ConditionStatus cs : mbc.getConditionStatuses()) {
                    Date startDate = cs.getMedicalBoardCase().getMedicalBoardSlot().getActualEndDateTime();
                    Long medicalBoardCaseId = cs.getMedicalBoardCase().getMedicalBoardCaseId();
                    cs.setMedicalBoardCase(null);
                    cs.setServiceman(null);
                    conditionStatusWrappers.add(new ConditionStatusWrapper(cs, medicalBoardCaseId, startDate));
                }

                mbc.setBoardFindings(null);
                mbc.setStatementOfCase(null);
                mbc.setFollowUpMedicalBoardCase(null);
                mbc.setPreviousMedicalBoardCase(null);
                mbc.setMedicalBoardSlot(null);
                mbc.setConsultation(null);
                mbc.setConditionStatuses(null);
                
                medicalBoardCaseWrappers.add(new MedicalBoardCaseWrapper(mbc, scheduledStartDate, scheduledEndDate, chairman, conditionStatusWrappers));
                
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllServicemanMedicalBoardCasesRsp(medicalBoardCaseWrappers)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

}
