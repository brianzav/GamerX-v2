package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class DashboardController {
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("role") String role, Model model) {
        model.addAttribute("role", role);
        if ("ADMIN".equals(role)) {
            return "adminDashboard";
        } else {
            return "userDashboard";
        }
    }

    @GetMapping("/listUsers")
    public String listUsers(Model model) {
        List<UserModel> userList = userRepository.findAll();
        if (!userList.isEmpty()) {
            List<UserDTO> userDTOS = FourBitsUtils.convertModelToUserDTO(userList);
            return ResponseEntity.ok(userDTOS);
        }
        return ResponseEntity.noContent().build();
        return "listUsers"; // Retorna a view que lista os usuários
    }
}
