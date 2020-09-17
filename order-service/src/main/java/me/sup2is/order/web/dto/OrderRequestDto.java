package me.sup2is.order.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderRequestDto {

    @NotEmpty
    private String address;

    @Valid
    @NotEmpty
    private List<OrderItemRequestDto> orderItems;

    public Order toEntity() {
        List<OrderItem> items = orderItems.stream()
                .map(OrderItemRequestDto::toEntity)
                .collect(Collectors.toList());

        return Order.Builder.builder()
                .address(address)
                .orderItems(items)
                .build()
                .toEntity();
    }
}
