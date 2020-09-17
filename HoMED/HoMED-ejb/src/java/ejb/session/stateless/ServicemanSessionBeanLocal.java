/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Serviceman;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
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

    public Serviceman retrieveServicemanByNric(String nric) throws ServicemanNotFoundException;

    public Serviceman servicemanLogin(String nric, String password) throws ServicemanInvalidLoginCredentialException;

    public void changePassword(String nric, String oldPassword, String newPassword) throws ServicemanInvalidPasswordException, ServicemanNotFoundException;

    public Serviceman updateServiceman(Serviceman serviceman) throws ServicemanNotFoundException, ServicemanInvalidLoginCredentialException, UpdateServicemanException, InputDataValidationException, UnknownPersistenceException;

    public Serviceman retrieveServicemanById(Long servicemanId) throws ServicemanNotFoundException;

}
