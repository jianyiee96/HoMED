/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class EmployeeNricExistException extends Exception {
    
    public EmployeeNricExistException(){
    }
    
    public EmployeeNricExistException(String msg) {
        super(msg);
    }
    
}
