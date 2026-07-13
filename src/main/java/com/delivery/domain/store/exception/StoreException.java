package com.delivery.domain.store.exception;

import com.delivery.global.exception.BusinessException;

public class StoreException extends BusinessException {

    public StoreException(StoreErrorCode errorCode) {
        super(errorCode);
    }
}
