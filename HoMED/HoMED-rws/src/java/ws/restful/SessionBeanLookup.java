package ws.restful;

import ejb.session.stateless.EmployeeSessionBeanLocal;
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
    
}