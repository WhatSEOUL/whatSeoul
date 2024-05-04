package com.example.whatseoul.controller;


import com.example.whatseoul.dto.response.AlanResponseDto;
import com.example.whatseoul.service.WebClientService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@Slf4j
public class SseController {

    private final WebClientService webClientService;

    @Value("${alan.key}")
    private String alanId;

    @GetMapping("/alan")
    public Flux<ServerSentEvent<AlanResponseDto>> getAlanResponse(HttpServletResponse response){
        response.setContentType("text/event-stream");
        return webClientService.getResponse()
                .doOnNext(data -> {
                    // 받은 데이터를 로그로 출력
                    log.info("Received data: {}", data.getData().getContent());
                })
                .map(data -> ServerSentEvent.<AlanResponseDto>builder()
                        .id("eventId")
                        .event("eventType")
                        .data(data)
                        .build())
                .delayElements(Duration.ofMillis(50));
    }
}