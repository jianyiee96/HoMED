/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Serviceman;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.DeleteServicemanException;
import util.exceptions.InputDataValidationException;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.ServicemanEmailExistException;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import util.exceptions.ServicemanInvalidPasswordException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.ServicemanNricExistException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateServicemanException;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Local
public interface ServicemanSessionBeanLocal {

    public String createNewServiceman(Serviceman newServiceman) throws InputDataValidationException, ServicemanNricExistException, ServicemanEmailExistException, UnknownPersistenceException;

    public List<Serviceman> retrieveAllServicemen();
    
    public Serviceman retrieveServicemanByNric(String nric) throws ServicemanNotFoundException;

    public Serviceman servicemanLogin(String nric, String password) throws ServicemanInvalidLoginCredentialException;

    public void changePassword(String nric, String oldPassword, String newPassword) throws ServicemanInvalidPasswordException, ServicemanNotFoundException;

    public Serviceman updateServiceman(Serviceman serviceman) throws ServicemanNotFoundException, ServicemanInvalidLoginCredentialException, UpdateServicemanException, InputDataValidationException, UnknownPersistenceException;

    public Serviceman retrieveServicemanById(Long servicemanId) throws ServicemanNotFoundException;

    public void resetServicemanPassword(String nric, String email) throws ResetServicemanPasswordException;

    public Serviceman resetServicemanPasswordByAdmin(Serviceman currentServiceman) throws ResetServicemanPasswordException;

    public void deleteServiceman(Long servicemanId) throws ServicemanNotFoundException, DeleteServicemanException;

}
