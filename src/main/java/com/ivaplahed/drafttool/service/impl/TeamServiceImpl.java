package com.ivaplahed.drafttool.service.impl;

import com.ivaplahed.drafttool.dto.PlayerSearch;
import com.ivaplahed.drafttool.dto.TeamDTO;
import com.ivaplahed.drafttool.dto.TeamSearch;
import com.ivaplahed.drafttool.model.Player;
import com.ivaplahed.drafttool.model.Team;
import com.ivaplahed.drafttool.repository.TeamRepository;
import com.ivaplahed.drafttool.service.PlayerService;
import com.ivaplahed.drafttool.service.TeamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final PlayerService playerService;

    @Override
    public Optional<TeamDTO> getByName(String name) {
        return teamRepository.findByNameIgnoreCase(name).map(team ->
                new TeamDTO(team.getId(), team.getName(),
                        new PlayerSearch(team.getPlayerTop().getName(), team.getPlayerTop().getPosition(), team.getName()),
                        new PlayerSearch(team.getPlayerJungle().getName(), team.getPlayerJungle().getPosition(), team.getName()),
                        new PlayerSearch(team.getPlayerMid().getName(), team.getPlayerMid().getPosition(), team.getName()),
                        new PlayerSearch(team.getPlayerBot().getName(), team.getPlayerBot().getPosition(), team.getName()),
                        new PlayerSearch(team.getPlayerSupport().getName(), team.getPlayerSupport().getPosition(), team.getName())
                )
        );
    }

    @Override
    public Optional<Team> getById(UUID id) {
        return teamRepository.findById(id);
    }

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    @Override
    public List<TeamSearch> getAllSearch() {
        return teamRepository.findAllTeamSearch();
    }

    @Override
    public List<TeamSearch> getAllDefaultSearch() {
        return teamRepository.findAllDefaultTeamSearch();
    }

    @Override
    @Transactional
    public Team save(Team team) {

        Optional<Team> optionalTeam = teamRepository.findByNameIgnoreCase(team.getName());

        if (optionalTeam.isPresent()) {
            Team existingTeam = optionalTeam.get();
            existingTeam.setPlayerTop(team.getPlayerTop());
            existingTeam.setPlayerJungle(team.getPlayerJungle());
            existingTeam.setPlayerMid(team.getPlayerMid());
            existingTeam.setPlayerBot(team.getPlayerBot());
            existingTeam.setPlayerSupport(team.getPlayerSupport());
            return teamRepository.save(existingTeam);
        }

        if (team.getPlayerTop() != null) {
            Player savedPlayer = playerService.save(team.getPlayerTop());
            team.setPlayerTop(savedPlayer);
        }
        if (team.getPlayerJungle() != null) {
            Player savedPlayer = playerService.save(team.getPlayerJungle());
            team.setPlayerJungle(savedPlayer);
        }
        if (team.getPlayerMid() != null) {
            Player savedPlayer = playerService.save(team.getPlayerMid());
            team.setPlayerMid(savedPlayer);
        }
        if (team.getPlayerBot() != null) {
            Player savedPlayer = playerService.save(team.getPlayerBot());
            team.setPlayerBot(savedPlayer);
        }
        if (team.getPlayerSupport() != null) {
            Player savedPlayer = playerService.save(team.getPlayerSupport());
            team.setPlayerSupport(savedPlayer);
        }

        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        teamRepository.deleteById(id);
    }
}
