package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.model.ProductModel;
import br.com.senac.gamerx.repository.ProductRepository;
import br.com.senac.gamerx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stockist")
public class StockistController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<ProductModel> productPage = productRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 10));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "listProductsStockist";
    }

    @GetMapping("products/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        model.addAttribute("product", product);
        return "editProductStockist";
    }

    @PostMapping("products/update")
    public String updateStock(@ModelAttribute ProductModel product, RedirectAttributes redirectAttributes) {
        ProductModel existingProduct = productRepository.findById(product.getProductID())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + product.getProductID()));
        existingProduct.setStorage(product.getStorage());

        productRepository.save(existingProduct);
        redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");

        return "redirect:/stockist/products";
    }

}
