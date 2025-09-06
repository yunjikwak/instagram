package com.example.demo.controller;

import com.example.demo.controller.dto.UserLocalCreateRequestDto;
import com.example.demo.controller.dto.UserLocalLoginRequestDto;
import com.example.demo.controller.dto.UserResponseDto;
import com.example.demo.controller.dto.UserSocialCreateRequestDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> user(@PathVariable Integer id) {
        UserResponseDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/local")
    public ResponseEntity<UserResponseDto> localLogin(@RequestBody @Valid UserLocalLoginRequestDto request) {
        UserResponseDto user = userService.localLogin(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup/local")
    public ResponseEntity<UserResponseDto> localSignUp(@RequestBody @Valid UserLocalCreateRequestDto request) {
        UserResponseDto user = userService.localSignUp(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup/social")
    public ResponseEntity<UserResponseDto> socialSignUp(@RequestBody @Valid UserSocialCreateRequestDto request) {
        UserResponseDto user = userService.socialSignUp(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/terms")
    public ResponseEntity<Void> agreeTerms(@PathVariable Integer id) {
        userService.agreeTerms(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawUser(@PathVariable Integer id) {
        userService.withdrawUser(id);
        return ResponseEntity.noContent().build();
    }
}
