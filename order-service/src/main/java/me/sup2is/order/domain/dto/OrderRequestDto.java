package me.sup2is.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderRequestDto {

    private Long memberId;

    private List<OrderItemRequestDto> orderItems;

    public Order toEntity() {
        Order.Builder builder = new Order.Builder();
        builder.memberId(memberId);
        List<OrderItem> items = orderItems.stream()
                .map(OrderItemRequestDto::toEntity)
                .collect(Collectors.toList());
        builder.orderItems(items);
        return Order.createOrder(builder);
    }
}
