package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientModel, Long> {
    Optional<ClientModel> findByEmail(String email);
    Optional<ClientModel> findByCpf(String cpf);
}
