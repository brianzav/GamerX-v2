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
    @Autowired
    public DashboardController(UserRepository userRepository) {
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, HttpSession session) {
        UserModel loggedInUser = (UserModel) session.getAttribute("loggedAdmin");

        if (loggedInUser != null && loggedInUser.getRole() == UserModel.Role.ADMIN) {
            return "adminDashboard";
        } else if (loggedInUser != null && loggedInUser.getRole() == UserModel.Role.STOCKIST) {
            return "stockistDashboard";
        } else {
            return "redirect:/auth/login";
        }
    }
}
