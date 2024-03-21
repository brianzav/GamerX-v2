package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.RegisterDTO;
import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import br.com.senac.gamerx.service.HashingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final HashingService hashingService;

    @Autowired
    public AuthenticationController(UserRepository userRepository, HashingService hashingService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    @ModelAttribute("registerDTO")
    public RegisterDTO registerDTO() {
        return new RegisterDTO();
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO registerDTO, Model model) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email já está em uso.");
            return "register";
        }

        String hashedPassword = hashingService.hashPassword(registerDTO.getPassword());
        UserModel newUser = new UserModel(registerDTO.getNome(), registerDTO.getEmail(), hashedPassword, registerDTO.getCpf(), registerDTO.getRole(), true);
        userRepository.save(newUser);

        model.addAttribute("successMessage", "Usuário registrado com sucesso.");
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "loginUser";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Optional<UserModel> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            if (hashingService.checkPassword(password, user.getPassword())) {
                session.setAttribute("user", user);
                return "redirect:/dashboard";
            } else {
                model.addAttribute("loginError", "Senha incorreta");
                return "loginUser";
            }
        } else {
            model.addAttribute("loginError", "Usuário não encontrado");
            return "loginUser";
        }
    }

}
