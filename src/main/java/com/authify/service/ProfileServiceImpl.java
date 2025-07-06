package com.authify.service;

import com.authify.entity.UserEntity;
import com.authify.entity.UserRole;
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
        long expiry = System.currentTimeMillis() + 15 * 60 * 1000; // minutes

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(expiry);
        userRepository.save(user);

        try {
            emailService.sendResetOtpEmail(user.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send Email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
            throw new RuntimeException("Invalid Otp");
        }
        if (existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("Otp Expired");
        }
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);
        userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
    UserEntity existingUser=userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
    if(existingUser.getIsAccountVerified()!=null && existingUser.getIsAccountVerified())
    {
        return;
    }
    //Generate 6 digit otp
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        long expiry = System.currentTimeMillis() + (24 * 60*60 * 1000);

        //Update the profile
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiry);
        userRepository.save(existingUser);


        try {
            emailService.sendOtpEmail(existingUser.getEmail(),otp);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to send OTP email: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
    UserEntity existingUser=userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

        if(existingUser.getVerifyOtp()!=null && !existingUser.getVerifyOtp().equals(otp))
        {
            throw new RuntimeException("Invalid otp");
        }
        if(existingUser.getVerifyOtpExpireAt()<System.currentTimeMillis())
        {
            throw new RuntimeException("Otp expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);
        userRepository.save(existingUser);

    }

    @Override
    public UserEntity getUserByEmail(String email) {
       return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

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
                .role(request.getRole() != null ? request.getRole() : UserRole.USER)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }
}