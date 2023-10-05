package com.sharov.insta.mapper;

import com.sharov.insta.dto.UserReadDto;
import com.sharov.insta.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User object) {

        return UserReadDto.builder()
                .id(object.getId())
                .name(object.getName())
                .username(object.getUsername())
                .lastname(object.getLastname())
                .email(object.getEmail())
                .bio(object.getBio())
                .build();
    }
}
