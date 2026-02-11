package com.aynur.api_gateway_service.controller;


import com.aynur.commonlib.dto.PreferencesDto;
import com.aynur.commonlib.dto.ProfileDto;
import com.aynur.commonlib.dto.UserProfileAggregateDto;
import com.aynur.api_gateway_service.config.GatewayProperties;
import com.aynur.commonlib.constants.HeaderConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/user")
public class AggregateController {
    private final WebClient webClient;
    private final GatewayProperties props;
    public AggregateController(WebClient webClient, GatewayProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

    @GetMapping("/profile")
    public Mono<UserProfileAggregateDto> getProfileAggregate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestHeader(value = HeaderConstants.CORRELATION_ID, required = false) String correlationId
    ) {
        // Profile
        Mono<ProfileDto> profileMono = webClient.get()
                .uri(props.getProfile() + "/profiles/me")
                .headers(h -> forwardHeaders(h, authHeader, correlationId))
                .retrieve()
                .bodyToMono(ProfileDto.class)
                .timeout(Duration.ofSeconds(3));

        // Notification preferences  (fallback veririk)
        Mono<PreferencesDto> prefMono = webClient.get()
                .uri(props.getNotification() + "/notifications/preferences/me")
                .headers(h -> forwardHeaders(h, authHeader, correlationId))
                .retrieve()
                .bodyToMono(PreferencesDto.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorReturn(new PreferencesDto(false, false)); // servis down olsa da aggregate işləsin

        return Mono.zip(profileMono, prefMono)
                .map(t -> new UserProfileAggregateDto(t.getT1(), t.getT2()));
    }

    private void forwardHeaders(HttpHeaders headers, String authHeader, String correlationId) {
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }
        if (correlationId != null && !correlationId.isBlank()) {
            headers.set(HeaderConstants.CORRELATION_ID, correlationId);
        }
    }
}