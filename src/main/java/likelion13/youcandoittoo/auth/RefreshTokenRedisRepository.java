package likelion13.youcandoittoo.auth;

public interface RefreshTokenRedisRepository {

    boolean existsByKey(String key);

    void deleteByKey(String key);
}
