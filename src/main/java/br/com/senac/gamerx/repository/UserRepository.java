package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByCpf(String cpf);
    Page<UserModel> findByNomeContainingIgnoreCase(String name, Pageable pageable);


}
