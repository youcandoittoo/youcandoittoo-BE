package likelion13.youcandoittoo.user.repository;

import likelion13.youcandoittoo.user.entity.AuthProvider;
import likelion13.youcandoittoo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {

    Optional<AuthProvider> findByUsername(String username);

    Optional<AuthProvider> findByUser(User existingUser);
}
