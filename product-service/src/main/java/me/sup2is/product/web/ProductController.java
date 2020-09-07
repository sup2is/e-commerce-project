package me.sup2is.product.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.dto.ProductRequestDto;
import me.sup2is.product.domain.dto.ProductStockDto;
import me.sup2is.product.service.CategoryService;
import me.sup2is.product.service.ProductService;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> register(@RequestBody @Valid ProductRequestDto productRequestDto,
                                                  BindingResult bindingResult) {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        productService.register(productRequestDto.toEntity(), productRequestDto.getCategories());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @PutMapping("/stock")
    public ResponseEntity<JsonResult<?>> modifyStock(@RequestBody List<ProductStockDto> productStockDto) {
        productService.modifyStock(productStockDto);
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

}
