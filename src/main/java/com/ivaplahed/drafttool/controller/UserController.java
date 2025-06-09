package com.ivaplahed.drafttool.controller;

import com.ivaplahed.drafttool.dto.UserInfo;
import com.ivaplahed.drafttool.model.Authority;
import com.ivaplahed.drafttool.model.User;
import com.ivaplahed.drafttool.security.JwtUtils;
import com.ivaplahed.drafttool.security.controller.model.JwtResponse;
import com.ivaplahed.drafttool.security.model.UserDetailsImpl;
import com.ivaplahed.drafttool.security.repository.model.RefreshToken;
import com.ivaplahed.drafttool.security.service.RefreshTokenService;
import com.ivaplahed.drafttool.service.RoleService;
import com.ivaplahed.drafttool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userService.getByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new Exception("Username is already in use"));
        }

        String rawPassword = user.getPassword();

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setEnabled(true);
        newUser.setLastPasswordResetDate(LocalDate.now());
        newUser.setRoles(Set.of(roleService.getRoleByName(Authority.USER.toString())));

        userService.save(newUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), rawPassword)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(authentication);
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

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {

        Optional<UserInfo> userInfo = userService.getByUsernameInfo(username);

        if (userInfo.isPresent()) {
            return ResponseEntity.ok(userInfo.get());
        }
        return ResponseEntity.notFound().build();
    }

}
