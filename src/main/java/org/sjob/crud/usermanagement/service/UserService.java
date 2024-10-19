package org.sjob.crud.usermanagement.service;

import org.sjob.crud.usermanagement.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    Optional<List<User>> findByName(String name);

    Optional<User> findById(String id);

    User create(User userEntity);

    User modify(String id, User userEntity);

    boolean deleteById(String id);

    Optional<List<User>> findAll();
}
