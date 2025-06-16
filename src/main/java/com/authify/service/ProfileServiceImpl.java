package com.authify.service;

import com.authify.entity.UserEntity;
import com.authify.io.ProfileRequest;
import com.authify.io.ProfileResponse;
import com.authify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if (!userRepository.existsByEmail(request.getEmail())) {
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already Exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity exisintUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
        return convertToProfileResponse(exisintUser);
    }

    @Override
    public void sendResendOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Generate a 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        long expiry = System.currentTimeMillis() + 5 * 60 * 1000; // 5 minutes

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(expiry);
        userRepository.save(user);

        try {
            emailService.sendResetOtpEmail(user.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send Email");
        }
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .email(newProfile.getEmail())
                .password(newProfile.getPassword())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }
}