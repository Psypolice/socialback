package com.sharov.insta.dto;

import lombok.Value;

import java.util.Set;

@Value(staticConstructor = "of")
public class PostDto {

    Long id;
    String title;
    String caption;
    String location;
    String username;
    Integer likes;
    Set<String> usersLiked;

}
