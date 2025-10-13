package likelion13.youcandoittoo.auth.dto.social;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Map<String, Object> getKakaoAccount() {
        return getNestedMap(attributes, "kakao_account");
    }

    private Map<String, Object> getKakaoProfile() {
        return getNestedMap(getKakaoAccount(), "profile");
    }

    private Map<String, Object> getNestedMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Map ? (Map<String, Object>) value : Collections.emptyMap();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return StringUtils.defaultString((String) getKakaoAccount().get("email"), "NOT_PROVIDED");
    }

    @Override
    public String getName() {
        return StringUtils.defaultString((String) getKakaoProfile().get("nickname"), "NOT_PROVIDED");
    }
}
