package me.sup2is.product.repository;

import me.sup2is.product.domain.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long>
        , JpaSpecificationExecutor<Product> {

    @Modifying
    @Query("UPDATE Product p set p.stock = p.stock + :reduceCnt where p.id = :id")
    void reduceStock(long id, int reduceCnt);

}
