package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.ProductDTO;
import br.com.senac.gamerx.model.AddressModel;
import br.com.senac.gamerx.model.ClientModel;
import br.com.senac.gamerx.model.ProductModel;
import br.com.senac.gamerx.repository.ClientRepository;
import br.com.senac.gamerx.repository.ProductRepository;
import br.com.senac.gamerx.service.HashingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HashingService hashingService;

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<ProductDTO> productDTOs = productRepository.findAll().stream()
                .map(product -> {
                    String mainImageUrl = product.getProductImages().isEmpty() ? null :
                            product.getProductImages().get(0).getImagePath();
                    return new ProductDTO(
                            product.getProductID(),
                            product.getProductName(),
                            mainImageUrl,
                            product.getPrice());
                })
                .collect(Collectors.toList());
        model.addAttribute("products", productDTOs);
        return "listProductsClient";
    }

    @GetMapping("/products/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        model.addAttribute("product", product);
        model.addAttribute("images", product.getProductImages());
        return "telaProdClient";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        ClientModel cliente = new ClientModel();
        AddressModel enderecoInicial = new AddressModel();
        enderecoInicial.setEnderecoPadrao(true);
        cliente.getEnderecos().add(enderecoInicial);
        model.addAttribute("cliente", cliente);
        return "registerClient";
    }

    @PostMapping("/register")
    public String registerClient(@ModelAttribute ClientModel client, Model model) {
        if (clientRepository.findByEmail(client.getEmail()).isPresent() || clientRepository.findByCpf(client.getCpf()).isPresent()) {
            model.addAttribute("errorMessage", "Email ou CPF já está em uso.");
            return "registerClient";
        }
        client.setSenha(hashingService.hashPassword(client.getSenha()));

        if (!client.getEnderecos().isEmpty()) {
            client.getEnderecos().forEach(endereco -> {
                endereco.setCliente(client);
                endereco.setEnderecoPadrao(true);
            });
        }

        clientRepository.save(client);
        model.addAttribute("successMessage", "Cadastro realizado com sucesso!");
        return "redirect:/client/home";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        model.addAttribute("loggedUser", loggedUser);
        return "home";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<ClientModel> optionalClient = clientRepository.findByEmail(email);
        if (optionalClient.isPresent() && hashingService.checkPassword(password, optionalClient.get().getSenha())) {
            session.setAttribute("loggedUser", optionalClient.get());
            redirectAttributes.addFlashAttribute("loginSuccess", "Login successful");
            return "redirect:/client/home";
        } else {
            redirectAttributes.addFlashAttribute("loginError", "Credenciais inválidas");
            return "redirect:/client/home";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Encerra a sessão
        return "redirect:/client/home";
    }

    @PostMapping("/addAddress")
    public String addAddress(@ModelAttribute AddressModel address, HttpSession session, Model model) {
        ClientModel loggedInClient = (ClientModel) session.getAttribute("loggedUser");
        if (loggedInClient != null) {
            address.setCliente(loggedInClient);
            address.setEnderecoPadrao(loggedInClient.getEnderecos().isEmpty());
            loggedInClient.getEnderecos().add(address);
            clientRepository.save(loggedInClient);
            model.addAttribute("successMessage", "Endereço adicionado com sucesso!");
            return "redirect:/client/profile";
        } else {
            model.addAttribute("errorMessage", "Não autorizado.");
            return "login";
        }
    }


}
