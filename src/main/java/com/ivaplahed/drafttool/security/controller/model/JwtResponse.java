package com.ivaplahed.drafttool.security.controller.model;

import java.util.List;
import java.util.UUID;


public record JwtResponse(String accessToken, String refreshToken, UUID id, String username, String email,
                          List<String> roles) {}
