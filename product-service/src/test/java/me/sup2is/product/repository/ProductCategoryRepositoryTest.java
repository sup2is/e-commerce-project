package me.sup2is.product.repository;

import me.sup2is.product.domain.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductCategoryRepositoryTest {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Test
    public void query_check() {
        //given
        List<ProductCategory> allByCategoryNameIn =
                productCategoryRepository.findAllByCategoryNameIn(Arrays.asList("의류", "청바지"));
        //when
        //then
    }



}