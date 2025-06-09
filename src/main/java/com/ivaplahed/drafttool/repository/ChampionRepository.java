package com.ivaplahed.drafttool.repository;

import com.ivaplahed.drafttool.model.Champion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChampionRepository extends JpaRepository<Champion, UUID> {

    Optional<Champion> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM Champion c ORDER BY c.name")
    List<Champion> findAllOrderByName();

}
