package me.sup2is.product.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.dto.CategoryRequestDto;
import me.sup2is.product.service.CategoryService;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ResponseEntity<JsonResult<?>> addCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto,
                                                     BindingResult bindingResult) {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        categoryService.add(categoryRequestDto.toEntity());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

}
