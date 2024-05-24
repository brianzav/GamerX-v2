package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.UserModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    public DashboardController() {
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, HttpSession session) {
        UserModel loggedInUser = (UserModel) session.getAttribute("user");

        if (loggedInUser != null && loggedInUser.getRole() == UserModel.Role.ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (loggedInUser != null && loggedInUser.getRole() == UserModel.Role.STOCKIST) {
            return "redirect:/stockist/dashboard";
        } else {
            return "redirect:/auth/login";
        }
    }
}
