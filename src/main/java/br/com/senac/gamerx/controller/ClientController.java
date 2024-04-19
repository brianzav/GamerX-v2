package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.ProductDTO;
import br.com.senac.gamerx.model.AddressModel;
import br.com.senac.gamerx.model.ClientModel;
import br.com.senac.gamerx.model.ProductModel;
import br.com.senac.gamerx.repository.ClientRepository;
import br.com.senac.gamerx.repository.ProductRepository;
import br.com.senac.gamerx.service.ClientService;
import br.com.senac.gamerx.service.HashingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    private ClientService clientService;

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

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/client/home";
        }
        model.addAttribute("client", loggedUser);
        return "updateClientProfile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("client") ClientModel client, HttpSession session, RedirectAttributes redirectAttributes) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/client/home";
        }

        loggedUser.setNomeCompleto(client.getNomeCompleto());
        loggedUser.setDataNascimento(client.getDataNascimento());
        loggedUser.setGenero(client.getGenero());

        if (!client.getSenha().isEmpty()) {
            loggedUser.setSenha(hashingService.hashPassword(client.getSenha()));
        }

        clientRepository.save(loggedUser);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/client/profile";
    }

    @GetMapping("/manage-addresses")
    public String manageAddresses(HttpSession session, Model model) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/client/home";
        }
        ClientModel clientWithAddresses = clientService.getClientWithAddresses(loggedUser.getId());
        model.addAttribute("enderecos", clientWithAddresses.getEnderecos().stream()
                .filter(AddressModel::isEnderecoAtivo)
                .collect(Collectors.toList()));
        return "manageAddresses";
    }


    @PostMapping("/manage-addresses/add")
    public String addAddress(@ModelAttribute AddressModel newAddress, HttpSession session) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/client/home";
        }
        newAddress.setCliente(loggedUser);
        loggedUser.getEnderecos().add(newAddress);
        clientRepository.save(loggedUser);
        return "redirect:/client/manage-addresses";
    }

    @Transactional
    @PostMapping("/manage-addresses/update")
    public String updateAddressDefault(@RequestParam Long addressId, @RequestParam(required = false) boolean isDefault, HttpSession session) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/client/home";
        }

        AddressModel addressToUpdate = null;
        boolean found = false;

        for (AddressModel address : loggedUser.getEnderecos()) {
            if (address.getId() != null && address.getId().equals(addressId)) {
                addressToUpdate = address;
                found = true;
                break;
            }
        }

        if (found && addressToUpdate != null) {
            if (isDefault) {
                for (AddressModel address : loggedUser.getEnderecos()) {
                    address.setEnderecoPadrao(false);
                }
                addressToUpdate.setEnderecoPadrao(true);
            } else {
                addressToUpdate.setEnderecoPadrao(false);
            }
            clientRepository.save(loggedUser);
        } else {
            return "redirect:/client/manage-addresses?error=No address matched the provided ID.";
        }

        return "redirect:/client/manage-addresses";
    }



    //TODO ARRUMAR DELETAR E ATUALIZAR ENDEREC PADRAO
    @PostMapping("/manage-addresses/delete")
    public String deleteAddress(@RequestParam Long addressId, HttpSession session) {
        ClientModel loggedUser = (ClientModel) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/client/home";
        }

        AddressModel addressToDelete = loggedUser.getEnderecos().stream()
                .filter(a -> a.getId() != null && a.getId().equals(addressId))
                .findFirst()
                .orElse(null);

        if (addressToDelete != null) {
            addressToDelete.setEnderecoAtivo(false);
            clientRepository.save(loggedUser);
        } else {
            return "redirect:/client/manage-addresses?error=Address not found.";
        }

        return "redirect:/client/manage-addresses";
    }




}
