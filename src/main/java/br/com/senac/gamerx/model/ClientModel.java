package br.com.senac.gamerx.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class ClientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private LocalDate dataNascimento;
    private String genero;
    private String senha;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<AddressModel> enderecos;

    public ClientModel() {
        this.enderecos = new HashSet<>();
        AddressModel enderecoInicial = new AddressModel();
        enderecoInicial.setEnderecoPadrao(true);
        this.enderecos.add(enderecoInicial);
    }

    public void adicionarEndereco(AddressModel address) {
        enderecos.add(address);
        address.setCliente(this);
    }

    public void removerEndereco(AddressModel address) {
        enderecos.remove(address);
        address.setCliente(null);
    }
}
