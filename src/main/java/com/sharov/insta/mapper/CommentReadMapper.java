package com.sharov.insta.mapper;

import com.sharov.insta.dto.CommentDto;
import com.sharov.insta.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentReadMapper implements Mapper<Comment, CommentDto> {

    @Override
    public CommentDto map(Comment object) {

        return CommentDto.of(
                object.getId(),
                object.getMessage(),
                object.getUsername()
        );
    }
}
