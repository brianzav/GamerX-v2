package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.ShoppingCartModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartModel, Long> {
    Optional<ShoppingCartModel> findByClientId(Long clientId);

}
