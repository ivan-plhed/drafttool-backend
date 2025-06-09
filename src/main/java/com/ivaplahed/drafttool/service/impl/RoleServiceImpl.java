package com.ivaplahed.drafttool.service.impl;

import com.ivaplahed.drafttool.model.Authority;
import com.ivaplahed.drafttool.model.Role;
import com.ivaplahed.drafttool.repository.RoleRepository;
import com.ivaplahed.drafttool.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(Authority.valueOf(name));
    }

}
