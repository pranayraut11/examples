package com.security.oauth.authserver.service;

import com.security.oauth.authserver.dto.CreateClientDto;
import com.security.oauth.authserver.entity.Client;
import com.security.oauth.authserver.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService  implements RegisteredClientRepository {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Client clientFromDTO(CreateClientDto createClientDto){
        Client client = new Client();
        client.setClientId(createClientDto.getClientId());
        client.setClientSecret(passwordEncoder.encode(createClientDto.getClientSecret()));
        client.setAuthenticationMethods(createClientDto.getAuthenticationMethods());
        client.setAuthorizationGrantTypes(createClientDto.getAuthorizationGrantTypes());
        client.setRedirectUris(createClientDto.getRedirectUris());
        client.setScopes(createClientDto.getScopes());
        return client;
    }
    @Override
    public void save(RegisteredClient registeredClient) {

    }

    public void save(CreateClientDto createClientDto){
        clientRepository.save(clientFromDTO(createClientDto));
    }

    @Override
    public RegisteredClient findById(String id) {
        return clientRepository.findByClientId(id).map(Client::toRegisteredClient).orElseThrow();
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId).map(Client::toRegisteredClient).orElseThrow();
    }
}
