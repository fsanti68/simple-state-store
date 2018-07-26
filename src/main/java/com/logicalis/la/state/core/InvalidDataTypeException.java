package com.logicalis.la.state.core;

public class InvalidDataTypeException extends Exception {

	private static final long serialVersionUID = -5223086401823766015L;

	public InvalidDataTypeException() {
		super();
	}

	public InvalidDataTypeException(String message) {
		super(message);
	}

	public InvalidDataTypeException(Throwable cause) {
		super(cause);
	}

	public InvalidDataTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDataTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
