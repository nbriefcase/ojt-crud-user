package org.sjob.crud.usermanagement.service.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.logging.log4j.util.Strings;
import org.sjob.crud.usermanagement.dto.DtoUser;
import org.sjob.crud.usermanagement.entity.User;
import org.sjob.crud.usermanagement.repository.UserRepository;
import org.sjob.crud.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service("userManagementService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Validator entityValidator;
    private final String passwordRegex;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Validator entityValidator, @Value("${app.validation.regex.password}") String passwordRegex) {
        this.userRepository = userRepository;
        this.entityValidator = entityValidator;
        this.passwordRegex = passwordRegex;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<List<User>> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(UUID.fromString(id));
    }

    @Override
    @Transactional
    public User create(User userEntity) {
        validateUserEntity(userEntity, true);

        userEntity.setId(UUID.randomUUID());
        userEntity.setCreated(LocalDateTime.now().toString());
        userEntity.setActive(true);
        Optional.ofNullable(userEntity.getUserPhones())
                .ifPresent(phones -> phones.stream()
                        .filter(phone -> phone.getId() == null)
                        .forEach(phone -> phone.setId(UUID.randomUUID())));
        return userRepository.saveAndFlush(userEntity);
    }

    @Override
    @Transactional
    public User modify(String id, User userEntity) {
        if (Strings.isEmpty(id)) {
            throw new IllegalArgumentException("User Id is required.");
        }

        Optional<User> user = this.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        userEntity.setId(UUID.fromString(id));
        userEntity.setCreated(user.get().getCreated());
        userEntity.setLastLogin(user.get().getLastLogin());
        userEntity.setActive(user.get().isActive());
        userEntity.setToken(user.get().getToken());
        userEntity.setModified(LocalDateTime.now().toString());
        Optional.ofNullable(userEntity.getUserPhones())
                .ifPresent(phones -> phones.stream()
                        .filter(phone -> phone.getId() == null)
                        .forEach(phone -> {
                            phone.setId(UUID.randomUUID());
                            phone.setUserId(userEntity.getId());
                        }));
        validateUserEntity(userEntity, false);
        userEntity.setModified(LocalDateTime.now().toString());
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        return this.findById(id).map(user -> {
            userRepository.deleteById(user.getId());
            return true;
        }).orElse(false);
    }

    @Override
    public Optional<List<DtoUser>> findAll() {
        List<DtoUser> dtoUsers = new ArrayList<>();
        Optional.of(userRepository.findAll()).ifPresent(users -> users.forEach(user -> dtoUsers.add(DtoUser.fromEntity(user))));
        return Optional.of(dtoUsers);
    }


    private void validateUserEntity(User userEntity, boolean isNew) {
        if (Optional.ofNullable(userEntity.getEmail()).isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        Set<ConstraintViolation<User>> violations = entityValidator.validate(userEntity);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (isNew && userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        if (!Pattern.compile(passwordRegex).matcher(userEntity.getPassword()).matches()) {
            throw new IllegalArgumentException("Invalid password format.");
        }
    }
}
