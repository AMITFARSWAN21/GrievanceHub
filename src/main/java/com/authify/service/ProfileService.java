package com.authify.service;

import com.authify.io.ProfileRequest;
import com.authify.io.ProfileResponse;

public interface ProfileService {

    public ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendResendOtp(String email);
}

