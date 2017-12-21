package tech.lapsa.patterns.dao;

public class NotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public NotFound() {
    }

    public NotFound(final String message, final Throwable cause) {
	super(message, cause);
    }

    public NotFound(final String message) {
	super(message);
    }

    public NotFound(final Throwable cause) {
	super(cause);
    }
}
