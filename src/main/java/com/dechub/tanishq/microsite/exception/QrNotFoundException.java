package com.dechub.tanishq.microsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class QrNotFoundException extends RuntimeException {
    public QrNotFoundException(String message) {
        super(message);
    }
}