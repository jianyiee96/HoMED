/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Serviceman;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javax.ejb.Local;
import util.exceptions.ActivateServicemanException;
import util.exceptions.ChangeServicemanPasswordException;
import util.exceptions.CreateServicemanException;
import util.exceptions.DeleteServicemanException;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateServicemanException;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Local
public interface ServicemanSessionBeanLocal {

    public List<Serviceman> retrieveAllServicemen();

    public String createServiceman(Serviceman newServiceman) throws CreateServicemanException;

    public Serviceman retrieveServicemanById(Long servicemanId) throws ServicemanNotFoundException;
    
    public Serviceman retrieveServicemanByEmail(String email) throws ServicemanNotFoundException;

    public Serviceman updateServiceman(Serviceman serviceman) throws UpdateServicemanException;

    public void deleteServiceman(Long servicemanId) throws DeleteServicemanException;

    public Serviceman servicemanLogin(String email, String password) throws ServicemanInvalidLoginCredentialException;

    public void activateServiceman(String email, String password, String rePassword) throws ActivateServicemanException;

    public void changeServicemanPassword(String email, String oldPassword, String newPassword, String newRePassword) throws ChangeServicemanPasswordException;

    public void resetServicemanPassword(String email, String phoneNumber) throws ResetServicemanPasswordException;

    public Serviceman resetServicemanPasswordByAdmin(Serviceman currentServiceman) throws ResetServicemanPasswordException;
    
}
