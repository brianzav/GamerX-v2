package br.com.senac.gamerx.service;

import br.com.senac.gamerx.model.ClientModel;
import br.com.senac.gamerx.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    public void getClientWithAddresses_Found() {
        Long clientId = 1L;
        ClientModel expectedClient = new ClientModel();
        expectedClient.setId(clientId);
        expectedClient.setEnderecos(new HashSet<>());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(expectedClient));

        ClientModel actualClient = clientService.getClientWithAddresses(clientId);

        assertNotNull(actualClient, "O cliente deve ser encontrado");
        verify(clientRepository).findById(clientId);
    }

    @Test
    public void getClientWithAddresses_NotFound_ThrowsException() {
        Long clientId = 2L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            clientService.getClientWithAddresses(clientId);
        }, "Deve lançar NoSuchElementException se nenhum cliente for encontrado");

        verify(clientRepository).findById(clientId);
    }

}
