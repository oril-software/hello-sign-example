package co.oril.hellosign.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HelloSignLocalException extends RuntimeException {

	public HelloSignLocalException() {
	}

	public HelloSignLocalException(String message) {
		super(message);
	}

	public HelloSignLocalException(String message, Throwable cause) {
		super(message, cause);
	}

	public HelloSignLocalException(Throwable cause) {
		super(cause);
	}

}
