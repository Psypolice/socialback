package com.sharov.insta.service;

import com.sharov.insta.dto.UserCreateEditDto;
import com.sharov.insta.dto.UserReadDto;
import com.sharov.insta.entity.User;
import com.sharov.insta.mapper.UserCreateEditMapper;
import com.sharov.insta.mapper.UserReadMapper;
import com.sharov.insta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;

    public UserReadDto create(UserCreateEditDto userIn) {
        return Optional.of(userIn)
                .map(userCreateEditMapper::map)
                .map(userRepository::save)
                .map(userReadMapper::map)
                .orElseThrow(() -> new UsernameNotFoundException("The user " + userIn.getUsername() + " already exist. Please check credentials"));
    }

    public User update(UserCreateEditDto userCreateEditDto, Principal principal) {
        var user = getUserByPrincipal(principal);
        user.setName(userCreateEditDto.getFirstname());
        user.setLastname(userCreateEditDto.getLastname());
        user.setBio(userCreateEditDto.getBio());

        return userRepository.saveAndFlush(user);
    }

    private User getUserByPrincipal(Principal principal) {
        var name = principal.getName();
        return userRepository.findUsersByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + name));
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }


    @Override
    public UserDetails loadUserByUsername(String username) {

        return build(userRepository.findUsersByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found with username: " + username)));
    }

    public User loadUsersById(Long id) {
        return userRepository.findUsersById(id).orElseThrow(null);
    }

    public static User build(User user) {
        List<GrantedAuthority> authorities = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(toList());

        return User.builder()
                .id(user.getId())
                .username((user.getUsername()))
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    public UserReadDto getUserById(Long userId) {
        return userRepository.findUsersById(userId)
                .map(userReadMapper::map)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
