package com.ivaplahed.drafttool.scheduled;

import com.ivaplahed.drafttool.model.*;
import com.ivaplahed.drafttool.service.ChampionService;
import com.ivaplahed.drafttool.service.PlayerService;
import com.ivaplahed.drafttool.service.TeamService;
import com.ivaplahed.drafttool.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchDataScheduled {

    private static final String ROSTER_URL = "https://lol.fandom.com/api.php?action=cargoquery&tables=ScoreboardPlayers=SP&fields=SP.Name&format=json&order_by=SP.DateTime_UTC  DESC&limit=5&where=SP.Team=\"%s\"";
    private static final String PLAYER_INFO_URL = "https://lol.fandom.com/api.php?action=cargoquery&tables=Players=P&fields=P.ID ,P.Role,P.Name,P.Country&limit=1&format=json&where=P.ID=\"%s\"&order_by=P.Age DESC";
    private static final String IMAGE_FILE_URL = "https://lol.fandom.com/api.php?action=cargoquery&tables=PlayerImages=PI&fields=PI.FileName&where=PI.Link= \"%s\"&limit=500&format=json";
    private static final String IMAGE_URL = "https://lol.fandom.com/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File :%s";
    private static final String CHAMPION_POOL_URL = "https://lol.fandom.com/api.php?action=cargoquery&tables=ScoreboardPlayers=SP&fields=SP.Champion&format=json&order_by=SP.DateTime_UTC  DESC&limit=50&where=SP.Name=\"%s\"";
    private static final String CHAMPIONS_URL = "https://lol.fandom.com/api.php?action=cargoquery&tables=Champions=C&fields=C.Name,C.Title,C.Attributes&format=json&limit=500";

    @Value("#{'${league.teams}'.split(',')}")
    private List<String> teamNames;

    private final RestClient restClient = RestClient.create();

    private final PlayerService playerService;
    private final TeamService teamService;
    private final UserService userService;
    private final ChampionService championService;

//    @PostConstruct
//    @Transactional
//    public void init() {
//        fetchData();
//    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    void execute() {
        fetchData();
    }

    @Transactional
    void fetchData() {
        fetchAndSaveChampions();
        User user = userService.getByUsername("drafttool").orElse(null);

        for (String teamName : teamNames) {
            log.info("Fetching data for team {}", teamName);
            List<String> roster = fetchRosterForTeam(teamName);
            List<Player> players = buildPlayersFromRoster(roster, user);

            Team team = createTeam(teamName, user);
            assignPlayersToTeam(team, players);
            saveTeamWithPlayers(team);
        }
    }

    private void fetchAndSaveChampions() {
        CargoQueryResponse response = getChampionsResponse();
        if (response != null && response.cargoquery() != null) {
            response.cargoquery().forEach(entry -> {
                if (entry.title() != null) {

                    String[] roles = entry.title().Attributes().split(",");

                    championService.save(Champion.builder()
                            .name(entry.title().Name().toUpperCase())
                            .title(entry.title().Title())
                            .role1(roles[0].trim())
                            .role2(roles.length > 1 ? roles[1].trim() : null)
                            .build());
                }
            });
        }
    }

    private CargoQueryResponse getChampionsResponse() {
        return restClient.get()
                .uri(CHAMPIONS_URL)
                .retrieve()
                .body(CargoQueryResponse.class);
    }

    private List<String> fetchRosterForTeam(String teamName) {
        CargoQueryResponse response = restClient.get()
                .uri(String.format(ROSTER_URL, teamName))
                .retrieve()
                .body(CargoQueryResponse.class);

        if (response == null || response.cargoquery() == null) return Collections.emptyList();

        return deduplicate(response.cargoquery().stream()
                .map(entry -> entry.title().Name())
                .filter(Objects::nonNull)
                .toList());
    }

    private List<Player> buildPlayersFromRoster(List<String> roster, User user) {
        List<Player> players = new ArrayList<>();

        for (String playerName : new LinkedHashSet<>(roster)) {
            Optional<Player> playerOpt = createPlayer(playerName, user);
            playerOpt.ifPresent(players::add);
        }

        return players;
    }

    private Optional<Player> createPlayer(String playerName, User user) {
        log.info("Fetching data for player {}", playerName);
        CargoQueryResponse playerInfo = fetchPlayerInfo(playerName);
        if (playerInfo == null || playerInfo.cargoquery() == null) return Optional.empty();

        return playerInfo.cargoquery().stream()
                .map(CargoQueryResponse.CargoEntry::title)
                .filter(this::isValidPlayerData)
                .findFirst()
                .map(title -> {
                    Optional<Player> existingPlayer = playerService.getByName(title.ID());

                    Player player;
                    if (existingPlayer.isPresent()) {
                        player = existingPlayer.get();
                        player.setRealName(title.Name());
                        player.setPosition(Position.valueOf(title.Role().toUpperCase()));
                        player.setCountry(title.Country());
                        player.setImage(fetchImageUrl(title.ID()));
                    } else {
                        player = Player.builder()
                                .name(title.ID())
                                .realName(title.Name())
                                .position(Position.valueOf(title.Role().toUpperCase()))
                                .country(title.Country())
                                .createdBy(user)
                                .image(fetchImageUrl(title.ID()))
                                .build();
                    }
                    return player;
                });
    }

    private boolean isValidPlayerData(CargoQueryResponse.CargoEntry.Title title) {
        return title.ID() != null && !title.ID().isBlank() &&
                title.Name() != null && !title.Name().isBlank() &&
                title.Role() != null && !title.Role().isBlank() &&
                title.Country() != null && !title.Country().isBlank();
    }

    private CargoQueryResponse fetchPlayerInfo(String playerName) {
        return restClient.get()
                .uri(String.format(PLAYER_INFO_URL, playerName))
                .retrieve()
                .body(CargoQueryResponse.class);
    }

    private String fetchImageUrl(String playerId) {
        try {
            CargoQueryResponse imageFileResponse = restClient.get()
                    .uri(String.format(IMAGE_FILE_URL, playerId))
                    .retrieve()
                    .body(CargoQueryResponse.class);

            if (imageFileResponse != null && !imageFileResponse.cargoquery().isEmpty()) {
                for (CargoQueryResponse.CargoEntry entry : imageFileResponse.cargoquery()) {
                    String fileName = entry.title().FileName();
                }
            }

            if (imageFileResponse == null || imageFileResponse.cargoquery() == null || imageFileResponse.cargoquery().isEmpty()) {
                return null;
            }

            List<CargoQueryResponse.CargoEntry> entries = imageFileResponse.cargoquery();

            Optional<String> preferredFileName = entries.stream()
                    .map(entry -> entry.title().FileName())
                    .filter(Objects::nonNull)
                    .filter(fileName -> fileName.contains("2025"))
                    .findFirst();

            String fileName = preferredFileName.orElseGet(() ->
                    entries.get(0).title().FileName()
            );

            ImageQueryResponse imageQueryResponse = restClient.get()
                    .uri(String.format(IMAGE_URL, fileName))
                    .retrieve()
                    .body(ImageQueryResponse.class);

            return extractImageUrl(imageQueryResponse);

        } catch (Exception e) {
            log.error("Error fetching image URL for playerId: {}", playerId, e);
            return null;
        }
    }

    private String extractImageUrl(ImageQueryResponse response) {
        if (response == null || response.query() == null || response.query().pages() == null) {
            return null;
        }

        return response.query().pages().values().stream()
                .findFirst()
                .map(page -> page.imageinfo()[0].url())
                .orElse(null);
    }

    private Team createTeam(String teamName, User user) {
        Team team = new Team();
        team.setName(teamName.toUpperCase());
        team.setCreatedBy(user);
        return team;
    }

    private void assignPlayersToTeam(Team team, List<Player> players) {
        for (Player player : players) {
            Set<Champion> championPool = fetchChampionPool(player.getName());
            player.setChampions(championPool);
            player = playerService.save(player);

            Position position = player.getPosition();
            switch (position) {
                case TOP -> team.setPlayerTop(player);
                case JUNGLE -> team.setPlayerJungle(player);
                case MID -> team.setPlayerMid(player);
                case BOT -> team.setPlayerBot(player);
                case SUPPORT -> team.setPlayerSupport(player);
            }
        }
    }

    private Set<Champion> fetchChampionPool(String playerName) {
        CargoQueryResponse response = restClient.get()
                .uri(String.format(CHAMPION_POOL_URL, playerName))
                .retrieve()
                .body(CargoQueryResponse.class);

        Set<Champion> champions = new HashSet<>();
        if (response != null && response.cargoquery() != null) {
            for (CargoQueryResponse.CargoEntry entry : response.cargoquery()) {
                var title = entry.title();
                if (title != null && title.Champion() != null) {
                    championService.getByName(title.Champion()).ifPresent(champions::add);
                }
            }
        }
        return champions;
    }

    private void saveTeamWithPlayers(Team team) {
        teamService.save(team);
    }

    private <T> List<T> deduplicate(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

}