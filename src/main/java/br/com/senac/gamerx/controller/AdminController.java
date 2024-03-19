package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.ProductModel;
import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.ProductRepository;
import br.com.senac.gamerx.repository.UserRepository;
import br.com.senac.gamerx.utils.GamerXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UserModel> users = userRepository.findAll();
        model.addAttribute("users", users.stream().map(GamerXUtils::convertModelToUserDTO).collect(Collectors.toList()));
        return "listUsers"; // Template com a lista de usuários
    }

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<ProductModel> productPage = productRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 10));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "listProducts"; // Template com a lista de produtos
    }





    // Aqui vão os outros métodos para edição, ativação e desativação de usuários.
}

