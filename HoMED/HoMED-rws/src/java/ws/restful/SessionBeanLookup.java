package ws.restful;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ConditionStatusSessionBeanLocal;
import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SessionBeanLookup {

    private final String ejbModuleJndiPath;

    public SessionBeanLookup() {
        ejbModuleJndiPath = "java:global/HoMED/HoMED-ejb/";
    }

    public EmployeeSessionBeanLocal lookupEmployeeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (EmployeeSessionBeanLocal) c.lookup(ejbModuleJndiPath + "EmployeeSessionBean!ejb.session.stateless.EmployeeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public ServicemanSessionBeanLocal lookupServicemanSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ServicemanSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ServicemanSessionBean!ejb.session.stateless.ServicemanSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public FormTemplateSessionBeanLocal lookupFormTemplateSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (FormTemplateSessionBeanLocal) c.lookup(ejbModuleJndiPath + "FormTemplateSessionBean!ejb.session.stateless.FormTemplateSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public FormInstanceSessionBeanLocal lookupFormInstanceSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (FormInstanceSessionBeanLocal) c.lookup(ejbModuleJndiPath + "FormInstanceSessionBean!ejb.session.stateless.FormInstanceSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public MedicalCentreSessionBeanLocal lookupMedicalCentreSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (MedicalCentreSessionBeanLocal) c.lookup(ejbModuleJndiPath + "MedicalCentreSessionBean!ejb.session.stateless.MedicalCentreSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public ConsultationPurposeSessionBeanLocal lookupConsultationPurposeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ConsultationPurposeSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ConsultationPurposeSessionBean!ejb.session.stateless.ConsultationPurposeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public SlotSessionBeanLocal lookupSlotSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (SlotSessionBeanLocal) c.lookup(ejbModuleJndiPath + "SlotSessionBean!ejb.session.stateless.SlotSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public BookingSessionBeanLocal lookupBookingSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (BookingSessionBeanLocal) c.lookup(ejbModuleJndiPath + "BookingSessionBean!ejb.session.stateless.BookingSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public ConsultationSessionBeanLocal lookupConsultationSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ConsultationSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ConsultationSessionBean!ejb.session.stateless.ConsultationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public NotificationSessionBeanLocal lookupNotificationSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (NotificationSessionBeanLocal) c.lookup(ejbModuleJndiPath + "NotificationSessionBean!ejb.session.stateless.NotificationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public ConditionStatusSessionBeanLocal lookupConditionStatusSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ConditionStatusSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ConditionStatusSessionBean!ejb.session.stateless.ConditionStatusSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public MedicalBoardCaseSessionBeanLocal lookupMedicalBoardCaseSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (MedicalBoardCaseSessionBeanLocal) c.lookup(ejbModuleJndiPath + "MedicalBoardCaseSessionBean!ejb.session.stateless.MedicalBoardCaseSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    

}
