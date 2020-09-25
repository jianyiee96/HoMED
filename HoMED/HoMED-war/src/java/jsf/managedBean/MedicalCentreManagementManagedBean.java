/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import entity.MedicalCentre;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;

@Named(value = "medicalCentreManagementManagedBean")
@ViewScoped
public class MedicalCentreManagementManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private List<MedicalCentre> medicalCentres;

    public MedicalCentreManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();
    }

    public void dialogActionListener() {
        medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();
        PrimeFaces.current().ajax().update("formAllMedicalCentres:dataTableMedicalCentres");
    }

    public List<MedicalCentre> getMedicalCentres() {
        return medicalCentres;
    }

    public void setMedicalCentres(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }
}
