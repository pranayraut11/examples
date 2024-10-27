package com.example.metrics.controller;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@Slf4j
public class HomeController {

    @GetMapping("home")
    @Observed(name = "home.observed")
    public String home(@RequestParam String status,@RequestParam int time) {
        try {
            log.info("Sleeping for {} seconds", time);
            Thread.sleep(Duration.ofSeconds(time).toMillis());
        } catch (InterruptedException e) {
            log.error("Error occurred while sleeping {} ", e.getMessage());
        }
        if(status.equals("error")) {
            throw new RuntimeException("Error occurred");
        }
        return "Welcome to the home page";
    }
}
