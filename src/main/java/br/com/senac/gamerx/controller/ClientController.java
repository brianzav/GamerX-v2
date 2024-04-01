package br.com.senac.gamerx.controller;

import br.com.senac.gamerx.dto.ProductDTO;
import br.com.senac.gamerx.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ProductRepository productRepository;

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
}
