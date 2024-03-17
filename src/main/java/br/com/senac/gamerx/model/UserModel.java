package br.com.senac.gamerx.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    private String nome;
    private String email;
    private String password;
    private String cpf;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean active;

    public UserModel(String nome, String email, String password, String cpf, Role role, boolean active) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.role = role;
        this.active = active;
    }

    public enum Role {
        ADMIN("admin"), ESTOQUISTA("estoquista"), CLIENTE("cliente");

        Role(String role) {
        }
    }
}
