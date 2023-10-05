package com.sharov.insta.web;

import com.sharov.insta.dto.LoginDto;
import com.sharov.insta.dto.UserCreateEditDto;
import com.sharov.insta.payload.MessageResponse;
import com.sharov.insta.payload.response.JWTTokenSuccessResponse;
import com.sharov.insta.security.JWTTokenProvider;
import com.sharov.insta.security.SecurityConstants;
import com.sharov.insta.service.UserService;
import com.sharov.insta.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginDto loginRequest, BindingResult bindingResult) {

        var errors = responseErrorValidation.mapValidationService(bindingResult);
        if (ObjectUtils.isNotEmpty(errors)) {
            return errors;
        }

        var authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authenticate);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUsers(@Valid @RequestBody UserCreateEditDto signUpRequest, BindingResult bindingResult) {

        var errors = responseErrorValidation.mapValidationService(bindingResult);
        if (ObjectUtils.isNotEmpty(errors)) {
            return errors;
        }

        userService.create(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
}
