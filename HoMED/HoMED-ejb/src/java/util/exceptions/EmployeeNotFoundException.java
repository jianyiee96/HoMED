/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(){
    
    }
    
    public EmployeeNotFoundException(String msg) {
        super(msg);
    }
}
