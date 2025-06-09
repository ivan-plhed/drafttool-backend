package com.ivaplahed.drafttool.service.impl;

import com.ivaplahed.drafttool.dto.PlayerSearch;
import com.ivaplahed.drafttool.model.Player;
import com.ivaplahed.drafttool.repository.PlayerRepository;
import com.ivaplahed.drafttool.service.PlayerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Override
    public Optional<Player> getByName(String name) {
        return playerRepository.findByNameIgnoreCase(name);
    }

    public Optional<Player> getById(UUID id) {
        return playerRepository.findById(id);
    }

    @Override
    public List<PlayerSearch> getAllSearch() {
        return playerRepository.getAllPlayerSearch();
    }

    @Override
    public List<PlayerSearch> getAllDefaultSearch() {
        return playerRepository.getAllDefaultPlayerSearch();
    }

    @Override
    @Transactional
    public Player save(Player player) {

        Optional<Player> playerOptional = playerRepository.findByNameIgnoreCase(player.getName());

        if (playerOptional.isPresent()) {
            Player existingPlayer = playerOptional.get();
            existingPlayer.setRealName(player.getRealName());
            existingPlayer.setPosition(player.getPosition());
            existingPlayer.setCountry(player.getCountry());
            existingPlayer.setImage(player.getImage());
            existingPlayer.setChampions(player.getChampions());
            player = existingPlayer;
        }

        if (player.getImage() != null) {
            int position = player.getImage().lastIndexOf("/revision/");
            if (position > 0) {
                player.setImage(player.getImage().substring(0, position));
            }
        }

        return playerRepository.save(player);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        playerRepository.deleteById(id);
    }
}