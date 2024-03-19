package br.com.senac.gamerx.repository;

import br.com.senac.gamerx.model.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long>{
    Page<ProductModel> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
