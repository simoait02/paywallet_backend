package com.paylogic.paywalletlite.exception;

public class DoubleSpendException extends BusinessException {

    public DoubleSpendException(String message) {
        super("DOUBLE_SPEND", message);
    }
}