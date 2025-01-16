package com.erms.back.Exception;

public class PageOutOfBoundException extends BaseAppException {
    public PageOutOfBoundException(String message) {
        super(message);
    }

    public PageOutOfBoundException() {
        super("page number or size out of bound");
    }
}
