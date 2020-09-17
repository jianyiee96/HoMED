/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import entity.MedicalCentre;
import entity.OperatingHours;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import util.enumeration.DayOfWeekEnum;
import util.exceptions.InputDataValidationException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "medicalCentreManagementManagedBean")
@ViewScoped
public class MedicalCentreManagementManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private List<MedicalCentre> medicalCentres;

    private MedicalCentre medicalCentreToCreate;
    private MedicalCentre medicalCentreToManage;

//    private MedicalCentre selectedMedicalCentre;
    private Boolean isEditable;

    private LocalTime mondayOpening;
    private LocalTime mondayClosing;
    private LocalTime tuesdayOpening;
    private LocalTime tuesdayClosing;
    private LocalTime wednesdayOpening;
    private LocalTime wednesdayClosing;
    private LocalTime thursdayOpening;
    private LocalTime thursdayClosing;
    private LocalTime fridayOpening;
    private LocalTime fridayClosing;
    private LocalTime saturdayOpening;
    private LocalTime saturdayClosing;
    private LocalTime sundayOpening;
    private LocalTime sundayClosing;
    private LocalTime holidayOpening;
    private LocalTime holidayClosing;

    public MedicalCentreManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();

        medicalCentreToCreate = new MedicalCentre();
        System.out.println("medicalCentreToCreate.ops.size() = " + medicalCentreToCreate.getOperatingHours().size());
        
        initializeOperatingHours();
    }

    private void initializeOperatingHours() {
        mondayOpening = LocalTime.of(8, 30);
        tuesdayOpening = LocalTime.of(8, 30);
        wednesdayOpening = LocalTime.of(8, 30);
        thursdayOpening = LocalTime.of(8, 30);
        fridayOpening = LocalTime.of(8, 30);
        saturdayOpening = LocalTime.of(8, 30);
        sundayOpening = LocalTime.of(8, 30);
        holidayOpening = LocalTime.of(8, 30);
        mondayClosing = LocalTime.of(17, 30);
        tuesdayClosing = LocalTime.of(17, 30);
        wednesdayClosing = LocalTime.of(17, 30);
        thursdayClosing = LocalTime.of(17, 30);
        fridayClosing = LocalTime.of(17, 30);
        saturdayClosing = LocalTime.of(17, 30);
        sundayClosing = LocalTime.of(17, 30);
        holidayClosing = LocalTime.of(17, 30);
    }

    public void create() {
        System.out.println("Calling create....");

        try {
            String address = medicalCentreToCreate.getStreetName() + "!!!@@!!!"
                    + medicalCentreToCreate.getUnitNumber() + "!!!@@!!!"
                    + medicalCentreToCreate.getBuildingName() + "!!!@@!!!"
                    + medicalCentreToCreate.getCountry() + "!!!@@!!!"
                    + medicalCentreToCreate.getPostal();

            medicalCentreToCreate.setAddress(address);
            
            for (OperatingHours ohs : medicalCentreToCreate.getOperatingHours()) {
                System.out.println(ohs.getDayOfWeek() + ": " + ohs.getOpeningHours() + " --> " + ohs.getClosingHours());
            }

//            List<OperatingHours> medicalCentreOperatingHours = new ArrayList<>();
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.MONDAY, mondayOpening, mondayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.TUESDAY, tuesdayOpening, tuesdayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.WEDNESDAY, wednesdayOpening, wednesdayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.THURSDAY, thursdayOpening, thursdayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.FRIDAY, fridayOpening, fridayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.SATURDAY, saturdayOpening, saturdayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.SUNDAY, sundayOpening, sundayClosing));
//            medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.PUBLIC_HOLIDAY, holidayOpening, holidayClosing));
//
//            medicalCentreToCreate.setOperatingHours(medicalCentreOperatingHours);

            Long medicalCentreId = medicalCentreSessionBeanLocal.createNewMedicalCentre(medicalCentreToCreate);

            medicalCentres.add(medicalCentreToCreate);

            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Centre", "New medical centre is created!"));
            PrimeFaces.current().executeScript("PF('dialogCreateNewMedicalCentre').hide()");

            medicalCentreToCreate = new MedicalCentre();
            initializeOperatingHours();
        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new medical centre: " + ex.getMessage(), null));
        }

    }

    public void view(MedicalCentre medicalCentre) {
        System.out.println("Calling view....");

        isEditable = false;
        
//        selectedMedicalCentre = medicalCentre;
        medicalCentreToManage = medicalCentre;
        
        mondayOpening = medicalCentreToManage.getOperatingHours().get(0).getOpeningHours();
        mondayClosing = medicalCentreToManage.getOperatingHours().get(0).getClosingHours();
    }

    public void edit() {
        isEditable = true;
    }

    public void save() {
//        if (selectedMedicalCentre.getMedicalCentreId() == null) {

//        }
    }

    public List<MedicalCentre> getMedicalCentres() {
        return medicalCentres;
    }

    public void setMedicalCentres(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }

    public MedicalCentre getMedicalCentreToCreate() {
        return medicalCentreToCreate;
    }

    public void setMedicalCentreToCreate(MedicalCentre medicalCentreToCreate) {
        this.medicalCentreToCreate = medicalCentreToCreate;
    }

    public MedicalCentre getMedicalCentreToManage() {
        return medicalCentreToManage;
    }

    public void setMedicalCentreToManage(MedicalCentre medicalCentreToManage) {
        this.medicalCentreToManage = medicalCentreToManage;
    }

//    public MedicalCentre getSelectedMedicalCentre() {
//        return selectedMedicalCentre;
//    }
//
//    public void setSelectedMedicalCentre(MedicalCentre selectedMedicalCentre) {
//        this.selectedMedicalCentre = selectedMedicalCentre;
//    }
    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public LocalTime getMondayOpening() {
        return mondayOpening;
    }

    public void setMondayOpening(LocalTime mondayOpening) {
        this.mondayOpening = mondayOpening;
    }

    public LocalTime getMondayClosing() {
        return mondayClosing;
    }

    public void setMondayClosing(LocalTime mondayClosing) {
        this.mondayClosing = mondayClosing;
    }

    public LocalTime getTuesdayOpening() {
        return tuesdayOpening;
    }

    public void setTuesdayOpening(LocalTime tuesdayOpening) {
        this.tuesdayOpening = tuesdayOpening;
    }

    public LocalTime getTuesdayClosing() {
        return tuesdayClosing;
    }

    public void setTuesdayClosing(LocalTime tuesdayClosing) {
        this.tuesdayClosing = tuesdayClosing;
    }

    public LocalTime getWednesdayOpening() {
        return wednesdayOpening;
    }

    public void setWednesdayOpening(LocalTime wednesdayOpening) {
        this.wednesdayOpening = wednesdayOpening;
    }

    public LocalTime getWednesdayClosing() {
        return wednesdayClosing;
    }

    public void setWednesdayClosing(LocalTime wednesdayClosing) {
        this.wednesdayClosing = wednesdayClosing;
    }

    public LocalTime getThursdayOpening() {
        return thursdayOpening;
    }

    public void setThursdayOpening(LocalTime thursdayOpening) {
        this.thursdayOpening = thursdayOpening;
    }

    public LocalTime getThursdayClosing() {
        return thursdayClosing;
    }

    public void setThursdayClosing(LocalTime thursdayClosing) {
        this.thursdayClosing = thursdayClosing;
    }

    public LocalTime getFridayOpening() {
        return fridayOpening;
    }

    public void setFridayOpening(LocalTime fridayOpening) {
        this.fridayOpening = fridayOpening;
    }

    public LocalTime getFridayClosing() {
        return fridayClosing;
    }

    public void setFridayClosing(LocalTime fridayClosing) {
        this.fridayClosing = fridayClosing;
    }

    public LocalTime getSaturdayOpening() {
        return saturdayOpening;
    }

    public void setSaturdayOpening(LocalTime saturdayOpening) {
        this.saturdayOpening = saturdayOpening;
    }

    public LocalTime getSaturdayClosing() {
        return saturdayClosing;
    }

    public void setSaturdayClosing(LocalTime saturdayClosing) {
        this.saturdayClosing = saturdayClosing;
    }

    public LocalTime getSundayOpening() {
        return sundayOpening;
    }

    public void setSundayOpening(LocalTime sundayOpening) {
        this.sundayOpening = sundayOpening;
    }

    public LocalTime getSundayClosing() {
        return sundayClosing;
    }

    public void setSundayClosing(LocalTime sundayClosing) {
        this.sundayClosing = sundayClosing;
    }

    public LocalTime getHolidayOpening() {
        return holidayOpening;
    }

    public void setHolidayOpening(LocalTime holidayOpening) {
        this.holidayOpening = holidayOpening;
    }

    public LocalTime getHolidayClosing() {
        return holidayClosing;
    }

    public void setHolidayClosing(LocalTime holidayClosing) {
        this.holidayClosing = holidayClosing;
    }

}
