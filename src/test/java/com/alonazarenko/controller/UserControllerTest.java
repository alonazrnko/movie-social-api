package com.alonazarenko.controller;

import com.alonazarenko.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidation() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
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
    void shouldAcceptValidUser() {
        User user = baseUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectEmptyEmail() {
        User user = baseUser();
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNullEmail() {
        User user = baseUser();
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectInvalidEmail() {
        User user = baseUser();
        user.setEmail("wrongemail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectEmptyLogin() {
        User user = baseUser();
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectLoginWithSpaces() {
        User user = baseUser();
        user.setLogin("my login");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectFutureBirthday() {
        User user = baseUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
