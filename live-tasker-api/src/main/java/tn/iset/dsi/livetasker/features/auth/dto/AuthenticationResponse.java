package tn.iset.dsi.livetasker.features.auth.dto;


import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
){}