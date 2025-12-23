package com.prpo.chat.service;

import com.prpo.chat.entities.User;
import com.prpo.chat.service.dtos.LoginRequestDto;
import com.prpo.chat.service.dtos.RegisterRequestDto;
import com.prpo.chat.service.dtos.UserDto;
import com.prpo.chat.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
  @Autowired
  private final UserRepository userRepository;

  // TODO: use mappers
  public UserDto registerUser(final RegisterRequestDto request) {
    if(userRepository.existsByEmail(request.getEmail())) {
      throw new ResponseStatusException(
              HttpStatus.CONFLICT,
              "User already exists."
      );
    }
    if(userRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(
              HttpStatus.CONFLICT,
              "User already exists.");
    }

    // main info
    var user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    // TODO: call authentication service
    user.setPasswordHash(request.getPassword());

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

    // TODO: call authentication service
    final var hashedPassword = request.getPassword();

    if(hashedPassword.equals(user.getPasswordHash())) {
      final var userDto = new UserDto();

      userDto.setId(user.getId());
      userDto.setUsername(user.getUsername());
      userDto.setProfile(user.getProfile());

      return userDto;
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password.");
    }
  }
}
