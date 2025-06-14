package com.authify.controller;

import com.authify.io.AuthRequest;
import com.authify.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request)
    {
        try{
            authenticate(request.getEmail(),request.getPassword());
            final UserDetails userDetails=appUserDetailsService.loadUserByUsername(request.getEmail())


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
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        catch (Exception ex)
        {
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Authorizations Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }


    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

}
