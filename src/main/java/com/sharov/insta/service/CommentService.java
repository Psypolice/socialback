package com.sharov.insta.service;

import com.sharov.insta.dto.CommentDto;
import com.sharov.insta.entity.Comment;
import com.sharov.insta.entity.User;
import com.sharov.insta.exceptions.PostNotFoundException;
import com.sharov.insta.repository.CommentRepository;
import com.sharov.insta.repository.PostRepository;
import com.sharov.insta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Comment saveComment(Long postId, CommentDto commentDto, Principal principal) {
        var user = getUserByPrincipal(principal);
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for user: " + user.getEmail()));
        var comment = Comment.builder()
                .post(post)
                .usersId(user.getId())
                .username(user.getUsername())
                .message(commentDto.getMessage())
                .build();
        log.info("Saving comment for post: " + post.getId());

        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsForPost(Long postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        return commentRepository.findAllByPost(post);
    }

    public boolean deleteComment(Long commentId) {
        return commentRepository.findById(commentId)
                .map(entity -> {
                    commentRepository.delete(entity);
                    commentRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    private User getUserByPrincipal(Principal principal) {
        var name = principal.getName();

        return userRepository.findUsersByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + name));
    }
}
