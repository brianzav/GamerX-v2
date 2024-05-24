package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.RegisterDTO;
import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private UserRepository userRepository;  // Simulando o repositório de usuários

    @Mock
    private HashingService hashingService;  // Simulando o serviço de hashing de senha

    @Mock
    private HttpSession session;  // Simulação do HttpSession, para não dar erro de null

    @InjectMocks
    private AuthenticationController authenticationController;  // O controlador que estamos testando

    private Model model;  // Usado para adicionar atributos que serão mostrados nas views
    private RedirectAttributes redirectAttributes;  // Usado para adicionar atributos de redirecionamento

    @BeforeEach
    public void setUp() {
        model = new BindingAwareModelMap();  // Inicializando o model para os testes
        redirectAttributes = new RedirectAttributesModelMap();  // Inicializando os atributos de redirecionamento
    }

    @Test
    public void register_WithEmailAlreadyInUse_ShouldShowErrorMessage() {
        RegisterDTO registerDTO = new RegisterDTO();  // Dados do registro
        registerDTO.setEmail("user@example.com");  // Email que já existe no banco de dados

        // Configura o comportamento do mock para retornar um usuário se o email já existe
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new UserModel()));

        // Tentativa de registro
        String viewName = authenticationController.register(registerDTO, model);

        // Verifica se não salvamos o usuário (porque o email já existe)
        assertEquals("register", viewName);
        verify(userRepository, never()).save(any(UserModel.class));
        assertEquals("Email já está em uso.", model.getAttribute("errorMessage"));
    }

    @Test
    public void register_WithNewUser_ShouldRegisterSuccessfully() {
        RegisterDTO registerDTO = new RegisterDTO();  // Criando um novo registro DTO
        registerDTO.setNome("New User");
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setCpf("12345678900");
        registerDTO.setRole(UserModel.Role.ADMIN);

        // Configura os mocks para retornar um email não existente e simular o hashing
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(hashingService.hashPassword(any())).thenReturn("hashedPassword");

        // Processo de registro
        String viewName = authenticationController.register(registerDTO, model);

        // Verifica se o usuário é salvo e se a mensagem de sucesso é exibida
        assertEquals("register", viewName);
        verify(userRepository).save(any(UserModel.class));
        assertEquals("Usuário registrado com sucesso.", model.getAttribute("successMessage"));
    }

    @Test
    public void login_WithNonExistingEmail_ShouldShowErrorMessage() {
        // Configura o mock para simular que o email não existe
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // Tentativa de login com um email inexistente
        String viewName = authenticationController.login("nonexistent@example.com", "password", null, redirectAttributes, model);

        // Verifica se a mensagem de erro de "Usuário não encontrado" é exibida
        assertEquals("loginAdmin", viewName);
        assertEquals("Usuário não encontrado", model.getAttribute("loginError"));
    }

    @Test
    public void login_WithIncorrectPassword_ShouldShowErrorMessage() {
        UserModel user = new UserModel("User", "user@example.com", "correctHash", "12345678900", UserModel.Role.ADMIN, true);

        // Configura os mocks para simular um usuário existente mas com senha incorreta
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(hashingService.checkPassword(any(), any())).thenReturn(false);

        // Tentativa de login com senha incorreta
        String viewName = authenticationController.login("user@example.com", "wrongPassword", null, redirectAttributes, model);

        // Verifica se a mensagem de "Senha incorreta" é exibida
        assertEquals("loginAdmin", viewName);
        assertEquals("Senha incorreta", model.getAttribute("loginError"));
    }

    @Test
    public void login_WithCorrectCredentials_ShouldRedirectToDashboard() {
        UserModel user = new UserModel("User", "user@example.com", "correctHash", "12345678900", UserModel.Role.ADMIN, true);

        // Configura os mocks para simular um login bem-sucedido
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(hashingService.checkPassword(any(), any())).thenReturn(true);

        // Tentativa de login com credenciais corretas
        String viewName = authenticationController.login("user@example.com", "correctPassword", session, redirectAttributes, model);

        // Verifica se redireciona para o dashboard após o login bem-sucedido
        assertEquals("redirect:/dashboard", viewName);
        verify(session).setAttribute("user", user);  // Verifica se o usuário está sendo colocado na sessão corretamente
    }
}
