package com.social.group.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {

    int userId;

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;

    private LocalDate birthday;

    private List<Integer> followingIds;

    private int activate;
}
