package com.ivaplahed.drafttool.repository;

import com.ivaplahed.drafttool.dto.PlayerSearch;
import com.ivaplahed.drafttool.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByNameIgnoreCase(String name);

    @Query("""
            SELECT new com.ivaplahed.drafttool.dto.PlayerSearch(
                p.name,
                p.position,
                t.name
            )
            FROM Player p
            LEFT JOIN
                Team t
                ON t.playerTop.id = p.id
                OR t.playerJungle.id = p.id
                OR t.playerMid.id = p.id
                OR t.playerBot.id = p.id
                OR t.playerSupport.id = p.id
            WHERE p.createdBy.username = "drafttool"
            """)
    List<PlayerSearch> getAllDefaultPlayerSearch();

    @Query("""
            SELECT new com.ivaplahed.drafttool.dto.PlayerSearch(
                p.name,
                p.position,
                t.name
            )
            FROM Player p
            LEFT JOIN
                Team t
                ON t.playerTop.id = p.id
                OR t.playerJungle.id = p.id
                OR t.playerMid.id = p.id
                OR t.playerBot.id = p.id
                OR t.playerSupport.id = p.id
            """)
    List<PlayerSearch> getAllPlayerSearch();
}
