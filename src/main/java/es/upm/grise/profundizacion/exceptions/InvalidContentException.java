package es.upm.grise.profundizacion.exceptions;

public class InvalidContentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidContentException() {
        super();
    }

    public InvalidContentException(String message) {
        super(message);
    }

    public InvalidContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidContentException(Throwable cause) {
        super(cause);
    }
}
