package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.OrderModel;
import br.com.senac.gamerx.model.ProductModel;
import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.OrderRepository;
import br.com.senac.gamerx.repository.ProductRepository;
import br.com.senac.gamerx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/stockist")
public class StockistController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private boolean isStockistLoggedIn(HttpSession session) {
        UserModel stockist = (UserModel) session.getAttribute("user");
        return stockist != null && stockist.getRole() == UserModel.Role.STOCKIST;
    }

    @GetMapping("/dashboard")
    public String stockistDashboard(Model model, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        return "stockistDashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(defaultValue = "0") int page, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        Page<ProductModel> productPage = productRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 10));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "listProductsStockist";
    }

    @GetMapping("products/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        model.addAttribute("product", product);
        return "editProductStockist";
    }

    @PostMapping("products/update")
    public String updateStock(@ModelAttribute ProductModel product, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        ProductModel existingProduct = productRepository.findById(product.getProductID())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + product.getProductID()));
        existingProduct.setStorage(product.getStorage());

        productRepository.save(existingProduct);
        redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");

        return "redirect:/stockist/products";
    }

    @GetMapping("/orders")
    public String listOrders(Model model, @RequestParam(defaultValue = "0") int page, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        Page<OrderModel> orderPage = orderRepository.findAllByOrderByDateDesc(PageRequest.of(page, 10));
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        return "listOrdersStockist";
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        OrderModel order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
        model.addAttribute("order", order);
        return "editOrderStockist";
    }

    @PostMapping("/orders/update")
    public String updateOrderStatus(@ModelAttribute OrderModel order, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isStockistLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        OrderModel existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + order.getId()));
        existingOrder.setStatus(order.getStatus());

        orderRepository.save(existingOrder);
        redirectAttributes.addFlashAttribute("successMessage", "Status do pedido atualizado com sucesso!");

        return "redirect:/stockist/orders";
    }
}
