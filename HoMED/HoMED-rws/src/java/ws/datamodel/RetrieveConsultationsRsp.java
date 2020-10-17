/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.Consultation;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveConsultationsRsp {

    List<Consultation> consultations;

    public RetrieveConsultationsRsp() {
    }

    public RetrieveConsultationsRsp(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

}
