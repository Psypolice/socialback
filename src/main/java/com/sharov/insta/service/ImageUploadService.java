package com.sharov.insta.service;

import com.sharov.insta.entity.ImageModel;
import com.sharov.insta.entity.User;
import com.sharov.insta.exceptions.ImageNotFoundException;
import com.sharov.insta.repository.ImageRepository;
import com.sharov.insta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public ImageModel uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
        var user = getUserByPrincipal(principal);

        var userProfileImage = imageRepository.findByUsersId(user.getId()).orElse(null);
        if (ObjectUtils.isNotEmpty(userProfileImage)) {
            imageRepository.delete(userProfileImage);
        }
        var imageModel = ImageModel.builder()
                .usersId(user.getId())
                .imageBytes(compressBytes(file.getBytes()))
                .name(file.getOriginalFilename())
                .build();
        log.info("Uploading image profile to user {}", user.getUsername());

        return imageRepository.save(imageModel);
    }

    public ImageModel uploadImageForPost(MultipartFile file, Principal principal, Long postId) throws IOException {
        var user = getUserByPrincipal(principal);
        var post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());

        var imageModel = ImageModel.builder()
                .postId(post.getId())
                .imageBytes(compressBytes(file.getBytes()))
                .name(file.getOriginalFilename())
                .build();
        log.info("Uploading image for post {}", post.getId());

        return imageRepository.save(imageModel);
    }

    public ImageModel getImageToUser(Principal principal) {
        var user = getUserByPrincipal(principal);

        var imageModel = imageRepository.findByUsersId(user.getId()).orElse(null);
        if (ObjectUtils.isNotEmpty(imageModel)) {
            imageModel.setImageBytes(decompressByte(imageModel.getImageBytes()));
        }

        return imageModel;
    }

    public ImageModel getImageToPost(Long postId) {
        var imageModel = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Cannot find image for post"));
        if (ObjectUtils.isNotEmpty(imageModel)) {
            imageModel.setImageBytes(decompressByte(imageModel.getImageBytes()));
        }

        return imageModel;
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            log.error("Cannot compress Bytes");
        }
        log.info("Compressed Image Nyte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private static byte[] decompressByte(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            log.error("Cannot compress Bytes");
        }
        return outputStream.toByteArray();
    }

    private User getUserByPrincipal(Principal principal) {
        var name = principal.getName();

        return userRepository.findUsersByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + name));
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
