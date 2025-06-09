package com.ivaplahed.drafttool.controller;

import com.ivaplahed.drafttool.common.DrafttoolException;
import com.ivaplahed.drafttool.dto.PlayerSearch;
import com.ivaplahed.drafttool.model.Player;
import com.ivaplahed.drafttool.service.PlayerService;
import com.ivaplahed.drafttool.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;
    private final UserService userService;

    public PlayerController(PlayerService playerService, UserService userService) {
        this.playerService = playerService;
        this.userService = userService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String name) {
        return playerService.getByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable UUID id) {
        return playerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PlayerSearch>> getAllSearch() {
        return ResponseEntity.ok(playerService.getAllSearch());
    }

    @GetMapping("/default")
    public ResponseEntity<List<PlayerSearch>> getAllDefaultSearch() {
        return ResponseEntity.ok(playerService.getAllDefaultSearch());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(Player player, UriComponentsBuilder ucb) {

        if (player.getId() != null) {
            throw new DrafttoolException("To create a player id must be null");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        player.setCreatedBy(userService.getByUsername(userDetails.getUsername()).get());

        playerService.save(player);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(Player player, Authentication authentication) {

        if (player.getId() == null) {
            throw new DrafttoolException("To update a player id must not be null");
        }

        if (isOwner(player, authentication)) {
            playerService.save(player);
        }
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable String name, Authentication authentication) {

        Optional<Player> result = playerService.getByName(name);

        if (result.isPresent()) {
            Player player = result.get();

            if (isOwner(player, authentication)) {
                playerService.delete(player.getId());
                return ResponseEntity.noContent().build();
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DrafttoolException("Only the player that created the player can delete it"));
    }

    private boolean isOwner(Player player, Authentication authentication) {
        return player.getCreatedBy().getUsername().equals(authentication.getName()) || player.getCreatedBy().getUsername().equals("drafttool");
    }
}
