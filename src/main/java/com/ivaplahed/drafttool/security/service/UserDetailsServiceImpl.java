package com.ivaplahed.drafttool.security.service;

import com.ivaplahed.drafttool.model.User;
import com.ivaplahed.drafttool.security.model.UserDetailsImpl;
import com.ivaplahed.drafttool.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find the user with roles
        Optional<User> userWithRoles = userRepository.findByUsernameWithRoles(username);

        if (userWithRoles.isPresent()) {
            return UserDetailsImpl.build(userWithRoles.get());
        }

        // If not found with roles, try to find just the user
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return UserDetailsImpl.build(user.get());
        }

        // If still not found, throw exception
        throw new UsernameNotFoundException("User Not Found : " + username);
    }
}
