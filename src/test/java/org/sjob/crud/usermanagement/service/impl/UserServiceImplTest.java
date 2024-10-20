package org.sjob.crud.usermanagement.service.impl;

import jakarta.inject.Inject;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.sjob.crud.usermanagement.entity.User;
import org.sjob.crud.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceImplTest {

    @Value("${app.validation.regex.password}")
    String passwordRegex;

    @Inject
    private UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @BeforeEach
    public void init() {
        userServiceImpl = new UserServiceImpl(userRepository, Validation.buildDefaultValidatorFactory().getValidator(), passwordRegex);
    }

    @Test
    void findByEmail() {
        String email = "test1@google.com";
        String password = "aAqwe123421%";
        User userEntity = User.builder().email(email).password(password).build();
        User user = userServiceImpl.create(userEntity);
        Optional<User> userByEmail = userServiceImpl.findByEmail(email);

        assertTrue(userByEmail.isPresent());
        assertEquals(password, userByEmail.get().getPassword());

        userServiceImpl.deleteById(user.getId().toString());
    }

    @Test
    void findByName() {
        String email = "test1@google.com";
        String password = "aAqwe123421%";
        String name = "Neiro Diaz";
        User userEntity = User.builder().name(name).email(email).password(password).build();
        userServiceImpl.create(userEntity);
        Optional<List<User>> usersByName = userServiceImpl.findByName(name);

        assertTrue(usersByName.isPresent());
        assertEquals(1, usersByName.get().size());
        User user = usersByName.get().get(0);
        assertEquals(password, user.getPassword());

        //Find 2 users
        String email2 = "test2@google.com";
        String password2 = "aAqwe122221%";
        String name2 = "Neiro Diaz";
        User userEntity2 = User.builder().name(name2).email(email2).password(password2).build();
        userServiceImpl.create(userEntity2);
        Optional<List<User>> usersByName2 = userServiceImpl.findByName(name);

        assertTrue(usersByName2.isPresent());
        assertEquals(2, usersByName2.get().size());

        userServiceImpl.deleteById(usersByName2.get().get(0).getId().toString());
        userServiceImpl.deleteById(usersByName2.get().get(1).getId().toString());
    }

    @Test
    void findById() {
        String email = "test1@google.com";
        String password = "aAqwe123421%";
        String name = "John";
        User userEntity = User.builder().name(name).email(email).password(password).build();
        User user = userServiceImpl.create(userEntity);
        Optional<User> optionalUser = userServiceImpl.findById(user.getId().toString());

        assertTrue(optionalUser.isPresent());
        assertEquals(password, optionalUser.get().getPassword());

        userServiceImpl.deleteById(optionalUser.get().getId().toString());
    }

    @Test
    void create() {
        String email = "test1@google.com";
        String password = "aAqwe123421%";
        String name = "John";
        User userEntity = User.builder().name(name).email(email).password(password).build();
        User user = userServiceImpl.create(userEntity);
        assertNotNull(user);
        assertNotNull(user.getId());

        userServiceImpl.deleteById(user.getId().toString());
    }

    @Test
    void modify() {
        String email = "test1@google.com";
        String password = "aAqwe123421%";
        String passwordChanged = "aA12345678%";
        String name = "John";
        User userEntity = User.builder().name(name).email(email).password(password).build();
        User user = userServiceImpl.create(userEntity);
        Optional<User> userByEmail = userServiceImpl.findByEmail(user.getEmail());
        assertTrue(userByEmail.isPresent());
        Optional<User> optionalUser = userServiceImpl.findById(userByEmail.get().getId().toString());
        optionalUser.ifPresent(oUser -> oUser.setPassword(passwordChanged));
        assertTrue(optionalUser.isPresent());
        assertEquals(passwordChanged, optionalUser.get().getPassword());

        userServiceImpl.deleteById(optionalUser.get().getId().toString());
    }

    @Test
    void deleteById() {
        String email = "test1@google.com";
        String password = "aAqwe123421%";
        String name = "John";
        User userEntity = User.builder().name(name).email(email).password(password).build();
        User user = userServiceImpl.create(userEntity);
        userServiceImpl.deleteById(user.getId().toString());

        Optional<User> optionalUser = userServiceImpl.findById(user.getId().toString());
        assertTrue(optionalUser.isEmpty());
    }

    @Test
    void findAll() {
        User userEntity = User.builder().name("John").email("john@email.com").password("pAssword1%").build();
        User userEntity2 = User.builder().name("John Wick").email("johnwick@email.com").password("pAssword2%").build();
        User userEntity3 = User.builder().name("John Lennon").email("johnlennon@email.com").password("pAssword3%").build();
        // create users
        User user = userServiceImpl.create(userEntity);
        User user2 = userServiceImpl.create(userEntity2);
        User user3 = userServiceImpl.create(userEntity3);

        Optional<List<User>> optionalUsers = userServiceImpl.findAll();
        assertTrue(optionalUsers.isPresent());
        assertEquals(3, optionalUsers.get().size());

        userServiceImpl.deleteById(user.getId().toString());
        userServiceImpl.deleteById(user2.getId().toString());
        userServiceImpl.deleteById(user3.getId().toString());
    }
}
