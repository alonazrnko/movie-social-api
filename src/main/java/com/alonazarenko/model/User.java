package com.alonazarenko.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {

    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Friendship> friends;
}
