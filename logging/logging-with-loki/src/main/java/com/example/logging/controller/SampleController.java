package com.example.logging.controller;

import com.github.loki4j.slf4j.marker.LabelMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SampleController {

    @Scheduled(fixedRate = 5000)
    public void log() {
        log.info("Logging from first application");
        LabelMarker labelMarker = LabelMarker.of("counter", () -> "1");
        log.info(labelMarker, "Logging from first application with counter");
    }
}
