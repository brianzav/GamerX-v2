package br.com.senac.gamerx.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClientModelTest {

    private ClientModel client;

    @BeforeEach
    public void setUp() {
        client = new ClientModel();
    }

    @Test
    public void getDefaultAddress_ReturnsDefaultAddress() {
        AddressModel defaultAddress = client.getDefaultAddress();
        assertNotNull(defaultAddress, "Deve retornar um endereço padrão");
        assertTrue(defaultAddress.isEnderecoPadrao(), "O endereço retornado deve ser o padrão");
    }

    @Test
    public void getDefaultAddress_ReturnsNullWhenNoDefault() {
        // Remove o endereço padrão inicial e adiciona um não padrão
        client.getEnderecos().clear();
        AddressModel nonDefaultAddress = new AddressModel();
        nonDefaultAddress.setEnderecoPadrao(false);
        client.getEnderecos().add(nonDefaultAddress);

        AddressModel defaultAddress = client.getDefaultAddress();
        assertNull(defaultAddress, "Deve retornar null quando não há endereço padrão");
    }
}
