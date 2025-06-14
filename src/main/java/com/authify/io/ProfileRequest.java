package com.authify.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProfileRequest {
    @NotBlank(message = "Name should not be empty")
    private String name;

    @Email(message = "Enter valid email adress")
    @NotNull(message = "Name should not be empty")
    private String email;

    @Min(value = 6,message = "Password must be alteast of 6 characters")
    private String password;
}
