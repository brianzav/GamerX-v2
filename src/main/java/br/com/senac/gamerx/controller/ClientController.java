package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.ProductDTO;
import br.com.senac.gamerx.model.*;
import br.com.senac.gamerx.repository.*;
import br.com.senac.gamerx.service.ClientService;
import br.com.senac.gamerx.service.HashingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private AddressRepository addressRepository;

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

            ShoppingCartModel sessionCart = (ShoppingCartModel) session.getAttribute("cart");
            if (sessionCart != null && sessionCart.getClient() == null) {
                sessionCart.setClient(optionalClient.get());
                shoppingCartRepository.save(sessionCart);
            }

            String redirectUrl = (String) session.getAttribute("redirectUrl");
            session.removeAttribute("redirectUrl");
            return "redirect:" + (redirectUrl != null ? redirectUrl : "/client/home");
        } else {
            redirectAttributes.addFlashAttribute("loginError", "Credenciais inválidas");
            return "redirect:/client/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
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

    @GetMapping("/products/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        System.out.println("Product ID: " + product.getProductID());
        model.addAttribute("product", product);
        model.addAttribute("images", product.getProductImages());
        return "telaProdClient";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        ClientModel client = (ClientModel) session.getAttribute("loggedUser");
        ShoppingCartModel cart;

        if (client != null) {
            cart = shoppingCartRepository.findByClientId(client.getId())
                    .orElseGet(() -> {
                        ShoppingCartModel newCart = new ShoppingCartModel();
                        newCart.setClient(client);
                        shoppingCartRepository.save(newCart);
                        return newCart;
                    });
        } else {
            cart = (ShoppingCartModel) session.getAttribute("cart");
            if (cart == null) {
                cart = new ShoppingCartModel();
                session.setAttribute("cart", cart);
            }
        }

        model.addAttribute("cart", cart);
        model.addAttribute("userLoggedIn", client != null);
        if (client != null) {
            AddressModel defaultAddress = client.getEnderecos().stream()
                    .filter(AddressModel::isEnderecoPadrao)
                    .findFirst()
                    .orElse(null);
            model.addAttribute("defaultAddress", defaultAddress);
        }
        return "viewCart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productID, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        ClientModel client = (ClientModel) session.getAttribute("loggedUser");
        ShoppingCartModel cart = (ShoppingCartModel) session.getAttribute("cart");

        if (cart == null) {
            cart = new ShoppingCartModel();
            if (client != null) {
                cart.setClient(client);
            }
            session.setAttribute("cart", cart);
        }

        Optional<CartItemModel> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductID().equals(productID))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            ProductModel product = productRepository.findById(productID)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
            CartItemModel newItem = new CartItemModel();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setShoppingCart(cart);
            cart.getItems().add(newItem);
        }

        if (client != null) {
            shoppingCartRepository.save(cart);
        }
        return "redirect:/client/cart";
    }

    @PostMapping("/cart/remove")
    public String removeItemFromCart(@RequestParam Long itemId, HttpSession session) {
        ClientModel client = (ClientModel) session.getAttribute("loggedUser");
        ShoppingCartModel cart = shoppingCartRepository.findByClientId(client.getId()).orElse(null);

        if (cart != null) {
            cart.getItems().removeIf(item -> item.getId().equals(itemId));
            if (cart.getItems().isEmpty()) {
                shoppingCartRepository.delete(cart);
                session.setAttribute("cart", new ShoppingCartModel());
            } else {
                shoppingCartRepository.save(cart);
            }
        }

        return "redirect:/client/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        ClientModel client = (ClientModel) session.getAttribute("loggedUser");
        if (client == null) {
            return "redirect:/client/login";
        }

        ShoppingCartModel cart = (ShoppingCartModel) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/client/cart";
        }

        AddressModel defaultAddress = client.getDefaultAddress();
        model.addAttribute("userLoggedIn", true);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("cart", cart);
        return "checkoutPage";
    }

    @GetMapping("/payment")
    public String paymentPage(HttpSession session, Model model) {
        OrderModel order = (OrderModel) session.getAttribute("order");
        if (order == null) {
            return "redirect:/client/cart";
        }
        model.addAttribute("order", order);
        return "checkoutPage";
    }

    @PostMapping("/cart/checkout")
    public String finalizeOrder(HttpSession session,
                                @RequestParam("shippingOption") String shippingOption,
                                @RequestParam(value = "logradouro", required = false) String logradouro,
                                @RequestParam(value = "bairro", required = false) String bairro,
                                @RequestParam(value = "cidade", required = false) String cidade,
                                @RequestParam(value = "uf", required = false) String uf,
                                @RequestParam(value = "numero", required = false) String numero,
                                @RequestParam(value = "cep", required = false) String cep) {
        ClientModel client = (ClientModel) session.getAttribute("loggedUser");
        ShoppingCartModel cart = (ShoppingCartModel) session.getAttribute("cart");

        if (client == null || cart == null || cart.getItems().isEmpty()) {
            return "redirect:/client/login";
        }

        OrderModel order = new OrderModel();
        order.setClient(client);
        order.setTotal(cart.getTotal().add(new BigDecimal(shippingOption)));
        order.setStatus("Processando pagamento");
        order.setPaymentType("Cartão de crédito");

        AddressModel deliveryAddress;
        if (logradouro != null && bairro != null && cidade != null && uf != null && numero != null && cep != null) {
            deliveryAddress = new AddressModel();
            deliveryAddress.setLogradouro(logradouro);
            deliveryAddress.setBairro(bairro);
            deliveryAddress.setCidade(cidade);
            deliveryAddress.setUf(uf);
            deliveryAddress.setNumero(numero);
            deliveryAddress.setCep(cep);
        } else {
            deliveryAddress = client.getDefaultAddress();
        }
        order.setDeliveryAddress(deliveryAddress);

        for (CartItemModel item : cart.getItems()) {
            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        orderRepository.save(order);
        session.setAttribute("order", order);

        shoppingCartRepository.delete(cart);
        session.removeAttribute("cart");

        return "redirect:/client/payment";
    }


    @GetMapping("/order-summary")
    public String orderSummary(HttpSession session, Model model) {
        OrderModel order = (OrderModel) session.getAttribute("order");
        if (order == null) {
            return "redirect:/client/cart";
        }
        model.addAttribute("order", order);
        model.addAttribute("deliveryAddress", order.getDeliveryAddress());
        return "orderSummaryPage";
    }

    @PostMapping("/finalize-order")
    public String finalizeOrder(HttpSession session) {
        session.removeAttribute("order");
        return "redirect:/client/my-orders";
    }

    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        ClientModel client = (ClientModel) session.getAttribute("loggedUser");
        if (client == null) {
            return "redirect:/client/home";
        }

        List<OrderModel> orders = orderRepository.findByClientId(client.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders found for client ID: " + client.getId());
        }

        Date currentDate = new Date();
        model.addAttribute("currentDate", currentDate);

        model.addAttribute("orders", orders);
        return "myOrdersPage";
    }


    @GetMapping("/check-login")
    @ResponseBody
    public Map<String, Boolean> checkLogin(HttpSession session) {
        boolean isLoggedIn = session.getAttribute("loggedUser") != null;
        Map<String, Boolean> response = new HashMap<>();
        response.put("loggedIn", isLoggedIn);
        return response;
    }

    @GetMapping("/order-details/{orderId}")
    public String orderDetails(@PathVariable Long orderId, Model model) {
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + orderId));
        model.addAttribute("order", order);
        return "orderDetailsPage";
    }

}
