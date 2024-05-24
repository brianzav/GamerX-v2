package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressModel, Long> {

}
