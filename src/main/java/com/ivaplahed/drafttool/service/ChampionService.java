package com.ivaplahed.drafttool.service;

import com.ivaplahed.drafttool.model.Champion;

import java.util.List;
import java.util.Optional;

public interface ChampionService {

    Optional<Champion> getByName(String name);

    List<Champion> getAll();

    void save(Champion champion);

}
