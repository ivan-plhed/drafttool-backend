package com.ivaplahed.drafttool.service.impl;

import com.ivaplahed.drafttool.dto.UserInfo;
import com.ivaplahed.drafttool.model.User;
import com.ivaplahed.drafttool.repository.UserRepository;
import com.ivaplahed.drafttool.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserInfo> getByUsernameInfo(String username) {
        return userRepository.getUserInfoByUsername(username);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void save(User newUser) {
        userRepository.save(newUser);
    }
}
