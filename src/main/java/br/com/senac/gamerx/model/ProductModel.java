package br.com.senac.gamerx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productID;

    @NotBlank(message = "Attribute productname cannot be null")
    @Size(max = 200, message = "Attribute productname can have at maximum 200 characters")
    private String productName;

    @NotNull(message = "Attribute price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false)
    private double price;

    private List<String> productImages = new ArrayList<>();

    @Size(max = 2000, message = "Attribute productname can have at maximum 200 characters")
    private String description;

    private double rating;

    private int storage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    private boolean active = true;
}
