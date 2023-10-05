package com.sharov.insta.web;

import com.sharov.insta.dto.UserCreateEditDto;
import com.sharov.insta.dto.UserReadDto;
import com.sharov.insta.mapper.UserReadMapper;
import com.sharov.insta.service.UserService;
import com.sharov.insta.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@RestController
@RequestMapping("api/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ResponseErrorValidation responseErrorValidation;
    private final UserReadMapper userReadMapper;

    @GetMapping("/")
    public ResponseEntity<UserReadDto> getCurrentUser(Principal principal) {
        var user = userService.getCurrentUser(principal);
        var userReadDto = userReadMapper.map(user);

        return new ResponseEntity<>(userReadDto, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserReadDto> getUserProfile(@PathVariable String userId) {
        return new ResponseEntity<>(
                userService.getUserById(Long.parseLong(userId)),
                HttpStatus.OK
        );
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserCreateEditDto userCreateEditDto,
                                             BindingResult bindingResult,
                                             Principal principal) {
        var errors = responseErrorValidation.mapValidationService(bindingResult);
        if (isNotEmpty(errors)) {
            return errors;
        }

        var user = userService.update(userCreateEditDto, principal);
        var userUpdated = userReadMapper.map(user);

        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }
}
