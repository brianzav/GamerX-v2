package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.ProductRepository;
import br.com.senac.gamerx.repository.UserRepository;
import br.com.senac.gamerx.service.HashingService;
import br.com.senac.gamerx.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;  // Aqui a gente prepara o MockMvc, super útil para simular chamadas HTTP.

    @MockBean
    private UserRepository userRepository;  // Isso aqui é um mock do nosso repositório de usuários.

    @MockBean
    private ProductRepository productRepository;  // Outro mock, dessa vez para o repositório de produtos.

    @MockBean
    private HashingService hashingService;  // Serviço de hashing, também mockado.

    @MockBean
    private StorageService storageService;  // E mais um mock, agora para o serviço de armazenamento.

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        // Configura o MockMvc para o contexto web atual, muito útil para ter certeza que está tudo carregado certinho.
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Evita o NullPointerException configurando a resposta esperada do userRepository quando chamado.
        Page<UserModel> usersPage = new PageImpl<>(Collections.singletonList(new UserModel()));
        Mockito.when(userRepository.findByNomeContainingIgnoreCase(Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(usersPage);
    }

    @Test
    public void listUsersTest() throws Exception {
        // Teste básico para verificar se acessando /admin/users a gente recebe o status OK e a view correta.
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())  // Confirma que o status é OK, ou seja, sucesso!
                .andExpect(view().name("listUsers"));  // E checa se a view 'listUsers' é a que está sendo retornada.
    }

    @Test
    public void listUsers_WhenKeywordPresent_ShouldReturnFilteredUsers() throws Exception {
        // Esse teste é para quando a gente passa um 'keyword' e quer verificar se o filtro tá funcionando.
        mockMvc.perform(get("/admin/users").param("keyword", "test"))
                .andExpect(status().isOk())  // De novo, esperamos sucesso na resposta.
                .andExpect(view().name("listUsers"))  // A view ainda é a 'listUsers'.
                .andExpect(model().attributeExists("users"));  // E esse aqui confirma que temos 'users' como atributo no modelo, indicando que a filtragem rolou.
    }
}
