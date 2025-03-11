package kz.medet.userservice.repository;

import kz.medet.userservice.dto.ERole;
import kz.medet.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByIin(String iin);
    boolean existsByUsername(String username);
    boolean existsByIin(String iin);

    @Transactional
    void deleteByUsername(String username);

    List<User> findUsersByRole(ERole role);


}
