package com.authify.io;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message=" New password is Required")
    private String newPassword;

    @NotBlank(message="Opt is Required")
    private String otp;

    @NotBlank(message="Email is Required")
    private String email;
}
