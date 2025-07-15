package likelion13.youcandoittoo.global.exception.custom;

import likelion13.youcandoittoo.global.exception.error.ErrorCode;

public class ApiException extends BaseException {

    public ApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
