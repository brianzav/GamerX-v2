package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    List<OrderModel> findByClientId(Long clientId);
    Page<OrderModel> findAllByOrderByDateDesc(Pageable pageable);

}
