package com.sharov.insta.web;

import com.sharov.insta.dto.CommentDto;
import com.sharov.insta.mapper.CommentReadMapper;
import com.sharov.insta.payload.MessageResponse;
import com.sharov.insta.service.CommentService;
import com.sharov.insta.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentReadMapper commentReadMapper;
    private final ResponseErrorValidation responseErrorValidation;

    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable("postId") String postId,
                                                BindingResult bindingResult,
                                                Principal principal) {
        var errors = responseErrorValidation.mapValidationService(bindingResult);
        if (isNotEmpty(errors)) {
            return errors;
        }

        var comment = commentService.saveComment(Long.parseLong(postId), commentDto, principal);
        var createdComment = commentReadMapper.map(comment);

        return new ResponseEntity<>(createdComment, HttpStatus.OK);
    }

    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDto>> getAllCommentsToPost(@PathVariable("postId") String postId) {
        var commentDtoList = commentService.getAllCommentsForPost(Long.parseLong(postId))
                .stream()
                .map(commentReadMapper::map)
                .collect(Collectors.toList());

        return new ResponseEntity<>(commentDtoList, HttpStatus.OK);
    }

    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId) {
        commentService.deleteComment(Long.parseLong(commentId));

        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

}
