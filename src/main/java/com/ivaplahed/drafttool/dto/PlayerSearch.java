package com.ivaplahed.drafttool.dto;

import com.ivaplahed.drafttool.model.Position;

public record PlayerSearch(
        String name,
        Position position,
        String team
) {}
