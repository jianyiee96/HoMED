package ws.restful;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
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
    
    public SlotSessionBeanLocal lookupSlotPurposeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (SlotSessionBeanLocal) c.lookup(ejbModuleJndiPath + "SlotSessionBean!ejb.session.stateless.SlotSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
