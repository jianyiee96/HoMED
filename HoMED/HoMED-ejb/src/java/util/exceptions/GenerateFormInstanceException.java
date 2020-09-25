/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class GenerateFormInstanceException extends Exception {

    /**
     * Creates a new instance of <code>GenerateFormInstanceException</code>
     * without detail message.
     */
    public GenerateFormInstanceException() {
    }

    /**
     * Constructs an instance of <code>GenerateFormInstanceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public GenerateFormInstanceException(String msg) {
        super(msg);
    }
}
