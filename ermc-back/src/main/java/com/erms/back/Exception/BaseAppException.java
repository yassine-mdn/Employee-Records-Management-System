package com.erms.back.Exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseAppException extends RuntimeException{

    public BaseAppException(String message) {
        super(message);
        log.error(message);
    }

}
