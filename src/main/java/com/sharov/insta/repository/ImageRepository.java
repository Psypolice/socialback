package com.sharov.insta.repository;

import com.sharov.insta.entity.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Long> {

    Optional<ImageModel> findByUsersId(Long usersId);

    Optional<ImageModel> findByPostId(Long postId);
}
