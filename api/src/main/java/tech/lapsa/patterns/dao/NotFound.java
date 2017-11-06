package tech.lapsa.patterns.dao;

public class NotFound extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFound() {
	super();
    }

    public NotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
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
