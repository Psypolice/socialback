package com.sharov.insta.repository;

import com.sharov.insta.entity.Comment;
import com.sharov.insta.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    Comment findByIdAndUsersId(Long commentId, Long usersId);
}
