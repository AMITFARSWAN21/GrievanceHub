package com.authify.controller;

import com.authify.io.ProfileRequest;
import com.authify.io.ProfileResponse;
import com.authify.service.ProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class ProfileController {

    @Autowired
    private ProfileServiceImpl profileService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request)
    {
        ProfileResponse response=profileService.createProfile(request);
        //send welcome email
        return response;
    }

}
