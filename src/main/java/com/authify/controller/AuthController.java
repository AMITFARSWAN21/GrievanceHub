package com.authify.controller;

import com.authify.io.AuthRequest;
import com.authify.io.AuthResponse;
import com.authify.service.AppUserDetailsService;
import com.authify.service.ProfileService;
import com.authify.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;

    private final JwtUtil jwtUtil;
    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request)
    {
        try{
            authenticate(request.getEmail(),request.getPassword());
            final UserDetails userDetails=appUserDetailsService.loadUserByUsername(request.getEmail());
           final String jwtToken=jwtUtil.generateToken(userDetails);
            ResponseCookie cookie=ResponseCookie.from("jwt",jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite(("Strict"))
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .body(new AuthResponse(request.getEmail(),jwtToken));
        }
        catch (BadCredentialsException ex)
        {
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        catch (DisabledException ex)
        {
            ex.printStackTrace();
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // In AuthController.java
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Login Exception: " + ex.getMessage());// Add this line to log the real error
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Authorizations Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }


    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(
            @CurrentSecurityContext(expression = "authentication?.name") String email
    ) {
        return ResponseEntity.ok(email != null);
    }

    @PostMapping("/send-resend-otp")
    public ResponseEntity<?> sendResendOtp(@RequestParam String email) {
        try {
            profileService.sendResendOtp(email);
            return ResponseEntity.ok().body("OTP sent successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP: " + ex.getMessage());
        }
    }

}
