package com.ivaplahed.drafttool.service;

import com.ivaplahed.drafttool.dto.PlayerSearch;
import com.ivaplahed.drafttool.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerService {

    Optional<Player> getByName(String name);

    Optional<Player> getById(UUID id);

    List<PlayerSearch> getAllSearch();

    List<PlayerSearch> getAllDefaultSearch();

    Player save(Player player);

    void delete(UUID id);

}
