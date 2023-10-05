package com.sharov.insta.web;

import com.sharov.insta.dto.PostDto;
import com.sharov.insta.mapper.PostReadMapper;
import com.sharov.insta.payload.MessageResponse;
import com.sharov.insta.service.PostService;
import com.sharov.insta.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor
@CrossOrigin
public class PostController {

    private final PostReadMapper postReadMapper;
    private final PostService postService;
    private final ResponseErrorValidation responseErrorValidation;

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDto postDto,
                                             BindingResult bindingResult,
                                             Principal principal) {
        var errors = responseErrorValidation.mapValidationService(bindingResult);
        if (isNotEmpty(errors)) {
            return errors;
        }
        var post = postService.createPost(postDto, principal);
        var createdPost = postReadMapper.map(post);

        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        var postDtoList = postService.getAllPosts()
                .stream()
                .map(postReadMapper::map)
                .collect(toList());

        return new ResponseEntity<>(postDtoList, HttpStatus.OK);
    }

    @GetMapping("user/posts")
    public ResponseEntity<List<PostDto>> getAllPostsForUser(Principal principal) {
        var postDtoList = postService.getAllPostForUser(principal)
                .stream()
                .map(postReadMapper::map)
                .collect(toList());

        return new ResponseEntity<>(postDtoList, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDto> likePost(@PathVariable("postId") String postId,
                                            @PathVariable("username") String username) {
        var post = postService.likePost(parseLong(postId), username);
        var postDto = postReadMapper.map(post);

        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId, Principal principal) {
        postService.deletePost(parseLong(postId), principal);

        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }
}
