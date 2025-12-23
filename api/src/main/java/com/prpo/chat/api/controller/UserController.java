package com.prpo.chat.api.controller;

import com.prpo.chat.service.dtos.LoginRequestDto;
import com.prpo.chat.service.dtos.RegisterRequestDto;
import com.prpo.chat.service.dtos.UserDto;
import com.prpo.chat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users")
public class UserController {

  @Autowired
  private final UserService userService;

  @Operation(
          summary = "Register user"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "User registered successfully."),
          @ApiResponse(responseCode = "409", description = "User already exists."),
          @ApiResponse(responseCode = "500", description = "Failed to encrypt password.")
  })
  @PostMapping("/register")
  public ResponseEntity<UserDto> registerUser(
      @Valid @RequestBody final RegisterRequestDto request
  ) {
    final var createdUser = userService.registerUser(request);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @Operation(
          summary = "Login (with username)"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Login successfull."),
          @ApiResponse(responseCode = "403", description = "Password incorrect"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "500", description = "Failed to validate password")
  })
  @PostMapping("/login")
  public ResponseEntity<UserDto> login(
          @Valid @RequestBody final LoginRequestDto request
  ) {
    final var res = userService.login(request);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }


}
