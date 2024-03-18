package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.UserDTO;
import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import br.com.senac.gamerx.utils.GamerXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UserModel> users = userRepository.findAll();
        model.addAttribute("users", users.stream().map(GamerXUtils::convertModelToUserDTO).collect(Collectors.toList()));
        return "listUsers"; // Template com a lista de usuários
    }




    // Aqui vão os outros métodos para edição, ativação e desativação de usuários.
}

