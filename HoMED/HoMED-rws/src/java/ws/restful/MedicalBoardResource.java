/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.restful;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("MedicalBoard")
public class MedicalBoardResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MedicalBoardResource
     */
    public MedicalBoardResource() {
    }

    // To implement:   
    // retrieveAllServicemanStatuses (GET)
    // -> need to consume retrieveActiveConditionStatusByServiceman in ConditionStatusSessionBeanLocal
    // -> create a method to lookup the session bean  (refer to notification resource)
    //
    // retrieveAllServicemanMedicalBoardCases (GET)
    // -> need to create and consume a retrieveMedicalBoardCasesByServiceman in MedicalBoardCaseSessionBean
    // -> create a method to lookup the session bean (refet to notification resource)
    //
    //
    // Suggestion to refer to NotificationResource for GET methods
    
}
