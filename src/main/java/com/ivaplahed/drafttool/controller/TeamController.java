//package com.ivaplahed.drafttool.controller;
//
//import com.ivaplahed.drafttool.common.DrafttoolException;
//import com.ivaplahed.drafttool.dto.TeamDTO;
//import com.ivaplahed.drafttool.dto.TeamSearch;
//import com.ivaplahed.drafttool.model.Team;
//import com.ivaplahed.drafttool.security.model.UserDetailsImpl;
//import com.ivaplahed.drafttool.service.TeamService;
//import com.ivaplahed.drafttool.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/teams")
//@RequiredArgsConstructor
//public class TeamController {
//
//    private final TeamService teamService;
//    private final UserService userService;
//
//    @GetMapping
//    public ResponseEntity<?> getAllSearch() {
//        List<Team> teams = teamService.getAll();
//        return ResponseEntity.ok(teams);
//    }
//
//    @GetMapping("/default")
//    public ResponseEntity<List<TeamSearch>> getAllDefaultSearch() {
//        return ResponseEntity.ok(teamService.getAllDefaultSearch());
//    }
//
//    @GetMapping("/{name}")
//    public ResponseEntity<TeamDTO> getTeamById(@PathVariable String name) {
//        return teamService.getByName(name)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public void create(Team team) {
//
//        if (team.getId() != null) {
//            throw new DrafttoolException("To create a team id must be null");
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//        team.setCreatedBy(userService.getByUsername(userDetails.getUsername()).get());
//
//        teamService.save(team);
//    }
//
//    @PutMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update(Team team, Authentication authentication) {
//
//        if (team.getId() == null) {
//            throw new DrafttoolException("To update a team id must not be null");
//        }
//
//        if (isOwner(team, authentication)) {
//            teamService.save(team);
//        }
//    }
//
//    @DeleteMapping("/{name}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<?> delete(@PathVariable String name, Authentication authentication) {
//
//        Optional<TeamDTO> result = teamService.getByName(name);
//
//        if (result.isPresent()) {
//            Team team = teamService.getById(result.get().Id()).get();
//            if (isOwner(team, authentication)) {
//                teamService.delete(team.getId());
//                return ResponseEntity.noContent().build();
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DrafttoolException("Only the team that created the team can delete it"));
//    }
//
//    private boolean isOwner(Team team, Authentication authentication) {
//        return team.getCreatedBy().getUsername().equals(authentication.getName()) || team.getCreatedBy().getUsername().equals("drafttool");
//    }
//}

package com.ivaplahed.drafttool.controller;

import com.ivaplahed.drafttool.common.DrafttoolException;
import com.ivaplahed.drafttool.dto.TeamDTO;
import com.ivaplahed.drafttool.dto.TeamSearch;
import com.ivaplahed.drafttool.model.Team;
import com.ivaplahed.drafttool.security.model.UserDetailsImpl;
import com.ivaplahed.drafttool.service.TeamService;
import com.ivaplahed.drafttool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Import AccessDeniedException
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllSearch() {
        List<Team> teams = teamService.getAll();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/default")
    public ResponseEntity<List<TeamSearch>> getAllDefaultSearch() {
        return ResponseEntity.ok(teamService.getAllDefaultSearch());
    }

    @GetMapping("/{name}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable String name) {
        return teamService.getByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Team team) { // Added @RequestBody to correctly parse JSON

        if (team.getId() != null) {
            throw new DrafttoolException("To create a team id must be null");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Safely cast the principal to UserDetailsImpl
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            // This scenario should ideally be caught by Spring Security's AuthorizationFilter
            // before reaching here if .authenticated() is properly configured.
            // However, as a defensive measure against ClassCastException, we handle it.
            throw new AccessDeniedException("Full authentication with UserDetailsImpl is required to create a team.");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Ensure the user exists in the database
        Optional<com.ivaplahed.drafttool.model.User> creatingUser = userService.getByUsername(userDetails.getUsername());
        if (creatingUser.isEmpty()) {
            throw new DrafttoolException("Authenticated user not found in the database.");
        }

        team.setCreatedBy(creatingUser.get());

        teamService.save(team);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Team team, Authentication authentication) { // Added @RequestBody

        if (team.getId() == null) {
            throw new DrafttoolException("To update a team id must not be null");
        }

        if (isOwner(team, authentication)) {
            teamService.save(team);
        } else {
            throw new AccessDeniedException("Only the team owner can update the team.");
        }
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable String name, Authentication authentication) {

        Optional<TeamDTO> result = teamService.getByName(name);

        if (result.isPresent()) {
            Team team = teamService.getById(result.get().Id()).get();
            if (isOwner(team, authentication)) {
                teamService.delete(team.getId());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DrafttoolException("Only the team that created the team can delete it"));
            }
        }

        return ResponseEntity.notFound().build(); // If team not found, return 404
    }

    private boolean isOwner(Team team, Authentication authentication) {
        // Ensure authentication.getName() is not null before comparison
        String authenticatedUsername = authentication != null ? authentication.getName() : null;
        return authenticatedUsername != null &&
                (team.getCreatedBy().getUsername().equals(authenticatedUsername) || authenticatedUsername.equals("drafttool"));
    }
}
