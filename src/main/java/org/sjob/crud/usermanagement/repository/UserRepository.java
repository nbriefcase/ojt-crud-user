package org.sjob.crud.usermanagement.repository;

import org.sjob.crud.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<List<User>> findByName(String email);
}
