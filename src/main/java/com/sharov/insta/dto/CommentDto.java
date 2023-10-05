package com.sharov.insta.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value(staticConstructor = "of")
public class CommentDto {

    Long id;

    @NotEmpty
    String message;

    String username;
}
