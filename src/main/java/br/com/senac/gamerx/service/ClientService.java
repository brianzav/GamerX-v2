package br.com.senac.gamerx.service;

import br.com.senac.gamerx.model.ClientModel;
import br.com.senac.gamerx.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public ClientModel getClientWithAddresses(Long clientId) {
        ClientModel client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com o ID: " + clientId));
        client.getEnderecos().size(); // Acessando a coleção para inicializá-la
        return client;
    }
}
