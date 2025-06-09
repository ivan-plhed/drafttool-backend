package com.ivaplahed.drafttool.dto;

import java.util.List;
import java.util.UUID;

public record TeamDTO(UUID Id, String Name, PlayerSearch playerTop, PlayerSearch playerJungle, PlayerSearch playerMid, PlayerSearch playerBot, PlayerSearch playerSupport) {
}
