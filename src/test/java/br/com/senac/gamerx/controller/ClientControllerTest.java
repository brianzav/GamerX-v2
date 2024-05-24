package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.ClientModel;
import br.com.senac.gamerx.repository.ClientRepository;
import br.com.senac.gamerx.service.HashingService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ClientController clientController;

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new BindingAwareModelMap();
    }

    @Test
    public void login_WithCorrectCredentials_ShouldRedirectToHomePage() {
        // Configuração do cliente com credenciais corretas
        ClientModel client = new ClientModel();
        client.setEmail("valid@example.com");
        client.setSenha("validPassword");

        // Configuração do comportamento esperado dos mocks
        when(clientRepository.findByEmail("valid@example.com")).thenReturn(Optional.of(client));
        when(hashingService.checkPassword("validPassword", client.getSenha())).thenReturn(true);

        // Ação: tentativa de login
        String result = clientController.login("valid@example.com", "validPassword", session, redirectAttributes);

        // Verificação
        verify(session).setAttribute("loggedUser", client);
        assertEquals("redirect:/client/home", result);
    }

    @Test
    public void login_WithIncorrectPassword_ShouldRedirectToLoginWithError() {
        ClientModel client = new ClientModel();
        client.setEmail("valid@example.com");
        client.setSenha("hashedPassword");

        // Configuração do comportamento esperado dos mocks
        when(clientRepository.findByEmail("valid@example.com")).thenReturn(Optional.of(client));
        when(hashingService.checkPassword("invalidPassword", client.getSenha())).thenReturn(false);

        // Ação: tentativa de login
        String result = clientController.login("valid@example.com", "invalidPassword", session, redirectAttributes);

        // Verificação
        verify(session, never()).setAttribute(anyString(), any());
        verify(redirectAttributes).addFlashAttribute("loginError", "Credenciais inválidas");
        assertEquals("redirect:/client/login", result);
    }

    @Test
    public void login_WithNonexistentEmail_ShouldRedirectToLoginWithError() {
        // Configuração do comportamento esperado dos mocks
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Ação: tentativa de login
        String result = clientController.login("nonexistent@example.com", "anyPassword", session, redirectAttributes);

        // Verificação
        verify(session, never()).setAttribute(anyString(), any());
        verify(redirectAttributes).addFlashAttribute("loginError", "Credenciais inválidas");
        assertEquals("redirect:/client/login", result);
    }

    @Test
    public void registerClient_WithExistingCpf_ShouldReturnError() {
        ClientModel newClient = new ClientModel();
        newClient.setEmail("novo@usuario.com");
        newClient.setCpf("cpfExistente");
        newClient.setSenha("senha123");

        // Supondo que o CPF já esteja registrado
        when(clientRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(clientRepository.findByCpf("cpfExistente")).thenReturn(Optional.of(new ClientModel()));

        String viewName = clientController.registerClient(newClient, model);

        verify(clientRepository, never()).save(any(ClientModel.class));
        assertEquals("registerClient", viewName);
        assertEquals("Email ou CPF já está em uso.", model.asMap().get("errorMessage"));
    }


    @Test
    public void registerClient_WithValidInputs_ShouldSaveAllFieldsCorrectly() {
        ClientModel newClient = new ClientModel();
        newClient.setEmail("valido@usuario.com");
        newClient.setCpf("12345678901");
        newClient.setSenha("senha123");

        when(clientRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(clientRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(hashingService.hashPassword("senha123")).thenReturn("hashedSenha123");

        clientController.registerClient(newClient, model);

        verify(clientRepository).save(argThat(savedClient ->
                savedClient.getEmail().equals("valido@usuario.com") &&
                        savedClient.getCpf().equals("12345678901") &&
                        savedClient.getSenha().equals("hashedSenha123")
        ));
        assertEquals("Cadastro realizado com sucesso!", model.asMap().get("successMessage"));
    }
}
