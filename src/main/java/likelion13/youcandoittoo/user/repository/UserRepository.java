package likelion13.youcandoittoo.user.repository;

import likelion13.youcandoittoo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getByEmail(String email);

    User findByEmail(String email);
}
