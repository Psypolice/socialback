package com.sharov.insta.service;

import com.sharov.insta.dto.PostDto;
import com.sharov.insta.entity.Post;
import com.sharov.insta.entity.User;
import com.sharov.insta.exceptions.PostNotFoundException;
import com.sharov.insta.repository.ImageRepository;
import com.sharov.insta.repository.PostRepository;
import com.sharov.insta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public Post createPost(PostDto postDto, Principal principal) {
        var user = getUserByPrincipal(principal);
        var post = Post.builder()
                .users(user)
                .caption(postDto.getCaption())
                .location(postDto.getLocation())
                .title(postDto.getTitle())
                .likes(0)
                .build();

        log.info("Saving Post for User: {}", user.getEmail());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId, Principal principal) {
        var user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUsers(postId, user)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be find for user: " + user.getEmail()));
    }

    public List<Post> getAllPostForUser(Principal principal) {
        var user = getUserByPrincipal(principal);
        return postRepository.findAllByUsersOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long postId, String username) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        Optional<String> userLiked = post.getLikedUsers().stream()
                .filter(user -> user.equals(username)).findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal) {
        postRepository.delete(getPostById(postId, principal));
        imageRepository.findByPostId(postId).ifPresent(imageRepository::delete);
    }

    private User getUserByPrincipal(Principal principal) {
        var name = principal.getName();
        return userRepository.findUsersByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + name));
    }
}
