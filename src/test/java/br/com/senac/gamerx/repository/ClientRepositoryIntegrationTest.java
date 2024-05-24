package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.AddressModel;
import br.com.senac.gamerx.model.ClientModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ClientRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void testFindByEmail() {
        // Preparação do Cliente
        ClientModel client = new ClientModel();
        client.setEmail("test@example.com");
        client.setNomeCompleto("Test User");
        client.setCpf("12345678900");

        // Preparação do Endereço
        AddressModel address = new AddressModel();
        address.setCep("00000-000");
        address.setLogradouro("Rua Exemplo");
        address.setNumero("123");
        address.setBairro("Bairro Exemplo");
        address.setCidade("Cidade Exemplo");
        address.setUf("UF");
        address.setEnderecoPadrao(true);
        address.setEnderecoAtivo(true);
        address.setCliente(client);

        // Adiciona o endereço ao cliente
        client.setEnderecos(new HashSet<>(Arrays.asList(address)));

        // Persiste e executa o teste
        entityManager.persist(client);
        entityManager.flush();

        Optional<ClientModel> encontrado = clientRepository.findByEmail("test@example.com");

        assertTrue(encontrado.isPresent(), "Deveria encontrar um cliente pelo email");
        assertEquals("Test User", encontrado.get().getNomeCompleto(), "Deveria corresponder ao nome completo do cliente");
    }

    @Test
    public void testFindByCpf() {
        // Preparação do cliente
        ClientModel client = new ClientModel();
        client.setEmail("test2@example.com");
        client.setNomeCompleto("Another Test User");
        client.setCpf("98765432100");

        // Preparação do Endereço
        AddressModel address = new AddressModel();
        address.setCep("00000-000");
        address.setLogradouro("Rua Exemplo");
        address.setNumero("123");
        address.setBairro("Bairro Exemplo");
        address.setCidade("Cidade Exemplo");
        address.setUf("UF");
        address.setEnderecoPadrao(true);
        address.setEnderecoAtivo(true);
        address.setCliente(client);

        client.setEnderecos(new HashSet<>(Arrays.asList(address)));

        entityManager.persist(client);
        entityManager.flush();

        // Execução
        Optional<ClientModel> encontrado = clientRepository.findByCpf("98765432100");

        // Assertiva
        assertTrue(encontrado.isPresent(), "Deveria encontrar um cliente pelo CPF");
        assertEquals("Another Test User", encontrado.get().getNomeCompleto(), "Deveria corresponder ao nome completo do cliente");
    }


}
