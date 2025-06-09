package com.ivaplahed.drafttool.repository;

import com.ivaplahed.drafttool.dto.TeamSearch;
import com.ivaplahed.drafttool.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByNameIgnoreCase(String name);

    @Query("""
            SELECT new com.ivaplahed.drafttool.dto.TeamSearch(
                        t.id,
                t.name
            )
            FROM Team t
            WHERE t.createdBy.username = 'drafttool'
            """)
    List<TeamSearch> findAllDefaultTeamSearch();

    @Query("""
            SELECT new com.ivaplahed.drafttool.dto.TeamSearch(
                        t.id,
                t.name
            )
            FROM Team t
            """)
    List<TeamSearch> findAllTeamSearch();

}
