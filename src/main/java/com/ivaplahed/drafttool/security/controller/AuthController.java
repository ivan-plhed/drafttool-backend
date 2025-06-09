package com.ivaplahed.drafttool.security.controller;

import com.ivaplahed.drafttool.common.ErrorResponse;
import com.ivaplahed.drafttool.security.JwtUtils;
import com.ivaplahed.drafttool.security.controller.model.JwtResponse;
import com.ivaplahed.drafttool.security.controller.model.LoginRequest;
import com.ivaplahed.drafttool.security.model.UserDetailsImpl;
import com.ivaplahed.drafttool.security.repository.model.RefreshToken;
import com.ivaplahed.drafttool.security.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Authentication auth;

        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            logger.error("Could not authenticate user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Bad Credentials"));
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(auth);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponse(
                accessToken,
                refreshToken.getToken().toString(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Refresh token is required"));
        }

        try {
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        String accessToken = jwtUtils.generateAccessToken(auth);

                        Map<String, String> response = new HashMap<>();
                        response.put("accessToken", accessToken);
                        response.put("tokenType", "Bearer");

                        return ResponseEntity.ok(response);
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

}
