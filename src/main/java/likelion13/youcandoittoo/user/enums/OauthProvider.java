package likelion13.youcandoittoo.user.enums;

import likelion13.youcandoittoo.global.exception.custom.AuthException;
import likelion13.youcandoittoo.global.exception.error.ErrorCode;

public enum OauthProvider {
    NAVER,
    GOOGLE,
    KAKAO;

    static public OauthProvider toOauthProvider(String provider) {
        try {
            return OauthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrorCode.UNSUPPORTED_OPERATION, "지원하지 않는 공급자: " + provider);
        }
    }
}
