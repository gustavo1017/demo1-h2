package com.example.demo.client;

import com.example.demo.util.AuditRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuditClient {

    private final WebClient webClient;

    public Mono<Void> sendAudit(AuditRequestDto dto) {
        return webClient.post()
                .uri("http://localhost:8082/audit")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
