package me.sup2is.order.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.order.domain.dto.OrderRequestDto;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.service.OrderService;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> order(@RequestBody @Valid OrderRequestDto orderRequestDto,
                                               BindingResult bindingResult) throws OutOfStockException {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        orderService.order(orderRequestDto.toEntity());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }
}
