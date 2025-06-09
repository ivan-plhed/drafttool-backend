package com.ivaplahed.drafttool.service;

import com.ivaplahed.drafttool.dto.UserInfo;
import com.ivaplahed.drafttool.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<UserInfo> getByUsernameInfo(String username);

    Optional<User> getByUsername(String username);

    void save(User newUser);
}
