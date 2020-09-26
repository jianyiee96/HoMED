/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class CreateFormTemplateException extends Exception {

    /**
     * Creates a new instance of <code>CreateFormTemplateException</code>
     * without detail message.
     */
    public CreateFormTemplateException() {
    }

    /**
     * Constructs an instance of <code>CreateFormTemplateException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateFormTemplateException(String msg) {
        super(msg);
    }
}
