/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.ConsultationPurpose;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveAllConsultationPurposesRsp {
    
    List<ConsultationPurpose> consultationPurposes;

    public RetrieveAllConsultationPurposesRsp() {
    }

    public RetrieveAllConsultationPurposesRsp(List<ConsultationPurpose> consultationPurposes) {
        this.consultationPurposes = consultationPurposes;
    }

    public List<ConsultationPurpose> getConsultationPurposes() {
        return consultationPurposes;
    }

    public void setConsultationPurposes(List<ConsultationPurpose> consultationPurposes) {
        this.consultationPurposes = consultationPurposes;
    }

}
