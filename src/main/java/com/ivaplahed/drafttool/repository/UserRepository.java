package com.ivaplahed.drafttool.repository;

import com.ivaplahed.drafttool.dto.UserInfo;
import com.ivaplahed.drafttool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(String username);

    @Query("""
            SELECT new com.ivaplahed.drafttool.dto.UserInfo(
                u.username,
                concat(u.firstName, " ", u.lastName),
                u.email,
                u.phone
            )
            FROM User u
            WHERE u.username = :username
    """)
    Optional<UserInfo> getUserInfoByUsername(String username);

}
