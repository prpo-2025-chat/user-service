package com.prpo.chat.service;

import com.prpo.chat.entities.User;
import com.prpo.chat.service.dtos.*;
import com.prpo.chat.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    private final String ENCRYPTION_SERVICE_URL = "http://localhost:8082/password";

    // TODO: use mappers
    public UserDto registerUser(final RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists."
            );
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists.");
        }

        // main info
        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        final var hashedPassword = encryptPassword(request.getPassword());
        user.setPasswordHash(hashedPassword);

        // profile
        final var profile = new User.Profile();
        user.setProfile(profile);
        profile.setBio("");
        profile.setBirthdate(null);
        profile.setAvatarUrl("https://example.com/avatar.jpg");

        // default settings
        final var settings = new User.Settings();
        user.setSettings(settings);
        settings.setTheme(User.Theme.DARK);
        settings.setNotifications(true);

        //friends
        user.setFriends(List.of());

        user = userRepository.save(user);
        final var userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setProfile(user.getProfile());
        return userDto;
    }

    public UserDto login(final LoginRequestDto request) {
        final var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        final var isCorrect = validatePassword(request.getPassword(), user);

        if (isCorrect) {
            final var userDto = new UserDto();

            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setProfile(user.getProfile());

            return userDto;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password incorrect.");
        }
    }

    private String encryptPassword(String password) {
        try {
            final var request = new PasswordRequestDto();
            request.setPassword(password);

            final var response = restTemplate.postForObject(
                    ENCRYPTION_SERVICE_URL,
                    request,
                    PasswordHashDto.class
            );

            if (response == null || response.getHashedPassword() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to encrypt password: empty response");
            }
            return response.getHashedPassword();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to encrypt password: ", e
            );
        }
    }

    private Boolean validatePassword(String password, User user) {
        try {
            final var request = new PasswordHashDto();
            request.setPassword(password);
            request.setHashedPassword(user.getPasswordHash());

            final var response = restTemplate.postForObject(
                    ENCRYPTION_SERVICE_URL + "/validation",
                    request,
                    Boolean.class
            );

            if (response == null) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to validate password: empty response"
                );
            }
            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to validate password: ", e
            );
        }
    }
}
