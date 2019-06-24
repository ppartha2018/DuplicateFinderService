package com.homework.DuplicateFinder.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomError extends Exception {
	public CustomError() {
		super();
	}
	public CustomError(String message) {
		super(message);
	}
}
