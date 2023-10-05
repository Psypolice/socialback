package com.sharov.insta.mapper;

import com.sharov.insta.dto.PostDto;
import com.sharov.insta.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostReadMapper implements Mapper<Post, PostDto> {

    @Override
    public PostDto map(Post object) {

        return PostDto.of(
                object.getId(),
                object.getTitle(),
                object.getCaption(),
                object.getLocation(),
                object.getUsers().getUsername(),
                object.getLikes(),
                object.getLikedUsers()
        );
    }
}
