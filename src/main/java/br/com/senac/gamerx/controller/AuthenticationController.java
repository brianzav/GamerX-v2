package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.RegisterDTO;
import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());
        UserModel user = new UserModel(registerDTO.getNome(), registerDTO.getEmail(), encryptedPassword, registerDTO.getCpf(), registerDTO.getRole(), true);
        userRepository.save(user);

        model.addAttribute("successMessage", "Usuário registrado com sucesso.");
        return "register";
    }
}
