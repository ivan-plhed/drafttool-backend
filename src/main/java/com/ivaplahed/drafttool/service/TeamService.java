package com.ivaplahed.drafttool.service;

import com.ivaplahed.drafttool.dto.TeamDTO;
import com.ivaplahed.drafttool.dto.TeamSearch;
import com.ivaplahed.drafttool.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamService {

    Optional<TeamDTO> getByName(String name);

    Optional<Team> getById(UUID id);

    List<TeamSearch> getAllSearch();

    List<TeamSearch> getAllDefaultSearch();

    List<Team> getAll();

    Team save(Team team);

    void delete(UUID id);

}
