package likelion13.youcandoittoo.global.exception.custom;

import likelion13.youcandoittoo.global.exception.error.ErrorCode;

public class AuthException extends BaseException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
