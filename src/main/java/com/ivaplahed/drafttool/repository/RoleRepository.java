package com.ivaplahed.drafttool.repository;

import com.ivaplahed.drafttool.model.Authority;
import com.ivaplahed.drafttool.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByName(Authority name);
}
