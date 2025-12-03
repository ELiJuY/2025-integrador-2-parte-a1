package es.upm.grise.profundizacion.exceptions;

public class WrongFileTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WrongFileTypeException() {
        super();
    }

    public WrongFileTypeException(String message) {
        super(message);
    }

    public WrongFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongFileTypeException(Throwable cause) {
        super(cause);
    }
}
