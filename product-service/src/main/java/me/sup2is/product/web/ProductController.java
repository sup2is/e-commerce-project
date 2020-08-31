package me.sup2is.product.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.dto.ProductRequestDto;
import me.sup2is.product.service.CategoryService;
import me.sup2is.product.service.ProductService;
import me.sup2is.web.JsonResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> register(@RequestBody @Valid ProductRequestDto productRequestDto,
                                                  BindingResult bindingResult) {
        productService.register(productRequestDto.toEntity(), productRequestDto.getCategories());
        return null;
    }

}
