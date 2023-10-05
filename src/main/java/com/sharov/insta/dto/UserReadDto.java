package com.sharov.insta.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserReadDto {
    Long id;
    String name;
    String username;
    String lastname;
    String email;
    String bio;
}
