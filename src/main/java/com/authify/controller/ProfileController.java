package com.authify.controller;

import com.authify.entity.UserEntity;
import com.authify.io.ProfileRequest;
import com.authify.io.ProfileResponse;
import com.authify.service.EmailService;
import com.authify.service.ProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileServiceImpl profileService;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        // Send welcome email
        emailService.sendWelcomeEmail(response.getEmail(), request.getName());
        return response;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        return profileService.getProfile(email);
    }

    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getRoleByEmail(@RequestParam String email) {
        UserEntity user = profileService.getUserByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("role", user.getRole().name());
        return ResponseEntity.ok(response);
    }


}
