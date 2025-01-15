package com.erms.back.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PageOutOfBoundException extends RuntimeException {
    public PageOutOfBoundException(String message) {
        super(message);
    }

    public PageOutOfBoundException() {
        super("page number or size out of bound");
    }
}
