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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "listUsers";
    }

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<ProductModel> productPage = productRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 10));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "listProducts";
    }

    @PostMapping("/products")
    public String saveProduct(@ModelAttribute ProductModel product, RedirectAttributes redirectAttributes) {
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado com sucesso!");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/toggle-status/{id}")
    public String toggleProductStatus(@PathVariable("id") Long productId, RedirectAttributes redirectAttributes) {
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + productId));

        product.setActive(!product.isActive());
        productRepository.save(product);

        String message = product.isActive() ? "Produto ativado com sucesso!" : "Produto desativado com sucesso!";
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/products";
    }







}

