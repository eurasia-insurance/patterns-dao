package tech.lapsa.patterns.dao;

public class TooMuchFound extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TooMuchFound() {
	super();
    }

    public TooMuchFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

    public TooMuchFound(String message, Throwable cause) {
	super(message, cause);
    }

    public TooMuchFound(String message) {
	super(message);
    }

    public TooMuchFound(Throwable cause) {
	super(cause);
    }
}
