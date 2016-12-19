package pro.parseq.ghop.entities;

import java.util.Date;

public class Error {

	private final Date timestamp;
	private final int status;
	private final String error;
	private final Class<? extends Exception> exception;
	private final String message;
	private final String path;

	public Error(Date timestamp, int status, String error, Class<? extends Exception> exception, String message, String path) {

		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.exception = exception;
		this.message = message;
		this.path = path;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public Class<? extends Exception> getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}
}
