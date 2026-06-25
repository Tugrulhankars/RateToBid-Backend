package org.racetobid.racetobid.repository;

import org.racetobid.racetobid.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    java.util.Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
