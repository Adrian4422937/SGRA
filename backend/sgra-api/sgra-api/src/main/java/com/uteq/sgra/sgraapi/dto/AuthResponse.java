package com.uteq.sgra.sgraapi.dto;

import java.util.List;

public record AuthResponse(String token, List<String> roles) { }
