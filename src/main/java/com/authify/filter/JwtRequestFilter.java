package com.authify.filter;

import com.authify.service.AppUserDetailsService;
import com.authify.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    // ✅ Full exact paths of public endpoints
    private static final List<String> PUBLIC_URL_PREFIXES = List.of(
            "/api/v1.0/login",
            "/api/v1.0/register",
            "/api/v1.0/send-reset-otp",
            "/api/v1.0/reset-password",
            "/api/v1.0/logout",
            "/api/v1.0/resume",
            "/api/v1.0/roadmap"  // ✅ allow everything under /complaint
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ Debug log
        System.out.println("Request URI: " + path);

        // ✅ Skip JWT filter for public endpoints
        boolean isPublic = PUBLIC_URL_PREFIXES.stream().anyMatch(path::startsWith);
        if (isPublic) {
            System.out.println("Public URL hit. Skipping JWT validation.");
            filterChain.doFilter(request, response);
            return;
        }


        String jwt = null;
        String email = null;

        // ✅ Check Authorization header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        // ✅ Check cookies if header not found
        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // ✅ Validate token and set authentication context
        if (jwt != null) {
            email = jwtUtil.extractEmail(jwt);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
