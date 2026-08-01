package com.alonazarenko.controller;

import com.alonazarenko.exception.ValidationException;
import com.alonazarenko.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    private User baseUser() {
        User u = new User();
        u.setEmail("test@test.com");
        u.setLogin("testlogin");
        u.setName("Tester");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return u;
    }

    @Test
    void shouldCreateValidUser() {
        User user = baseUser();

        User created = controller.createUser(user);

        assertNotNull(created);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldRejectEmptyEmail() {
        User user = baseUser();
        user.setEmail("");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldRejectNullEmail() {
        User user = baseUser();
        user.setEmail(null);

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldRejectEmailWithoutAt() {
        User user = baseUser();
        user.setEmail("wrongemail");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldRejectEmptyLogin() {
        User user = baseUser();
        user.setLogin("");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldRejectLoginWithSpaces() {
        User user = baseUser();
        user.setLogin("my login");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldRejectFutureBirthday() {
        User user = baseUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldUseLoginAsNameIfEmpty() {
        User user = baseUser();
        user.setName("");

        User created = controller.createUser(user);

        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldFailOnNullUser() {
        assertThrows(NullPointerException.class, () -> controller.createUser(null));
    }
}
