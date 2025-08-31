package com.dechub.tanishq.microsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict is perfect for this
public class QrAlreadySubmittedException extends RuntimeException {
    public QrAlreadySubmittedException(String message) {
        super(message);
    }
}