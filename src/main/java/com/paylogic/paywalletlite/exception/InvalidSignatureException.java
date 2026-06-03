package com.paylogic.paywalletlite.exception;

public class InvalidSignatureException extends BusinessException {

    public InvalidSignatureException(String message) {
        super("INVALID_SIGNATURE", message);
    }
}