package com.security.oauth.authserver.controller;

import com.security.oauth.authserver.dto.CreateClientDto;
import com.security.oauth.authserver.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/debug")
    public String home(@RequestParam String code) {
        return code;
    }

    @PostMapping("/create")
    public String create(@RequestBody CreateClientDto createClientDto) {
        clientService.save(createClientDto);
        return "client created";
    }
}
