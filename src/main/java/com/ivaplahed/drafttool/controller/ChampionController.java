package com.ivaplahed.drafttool.controller;

import com.ivaplahed.drafttool.model.Champion;
import com.ivaplahed.drafttool.service.ChampionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/champions")
public class ChampionController {

    private final ChampionService championService;

    public ChampionController(ChampionService championService) {
        this.championService = championService;
    }

    @GetMapping
    public ResponseEntity<List<Champion>> getAllChampions() {
        return ResponseEntity.ok(championService.getAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Champion> getChampionByName(@PathVariable String name) {

        Optional<Champion> champion = championService.getByName(name);

        return champion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
