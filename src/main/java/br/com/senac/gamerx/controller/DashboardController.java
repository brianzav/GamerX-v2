package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final UserRepository userRepository;

    @Autowired
    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, HttpSession session) {
        UserModel loggedInUser = (UserModel) session.getAttribute("user");

        if (loggedInUser != null && loggedInUser.getRole() == UserModel.Role.ADMIN) {
            return "adminDashboard"; // O nome do template que mostra as opções para o admin
        } else if (loggedInUser != null) {
            return "userDashboard"; // Dashboard do usuário comum
        } else {
            return "redirect:/auth/login"; // Se não estiver logado, redireciona
        }
    }
}
