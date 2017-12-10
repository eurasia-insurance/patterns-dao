package tech.lapsa.patterns.dao;

public class NotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public NotFound() {
    }

    public NotFound(String message, Throwable cause) {
	super(message, cause);
    }

    public NotFound(String message) {
	super(message);
    }

    public NotFound(Throwable cause) {
	super(cause);
    }
}
