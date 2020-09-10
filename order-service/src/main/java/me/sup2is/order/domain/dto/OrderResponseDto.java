package me.sup2is.order.domain.dto;

import lombok.Getter;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {

    private long memberId;

    private String address;

    private long totalPrice;

    private OrderStatus orderStatus;

    private List<OrderItemResponseDto> orderItems;

    public OrderResponseDto(Order order) {
        this.memberId = order.getMemberId();
        this.address = order.getAddress();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus();
        this.orderItems = order.getOrderItems()
                .stream()
                .map(OrderItemResponseDto::new)
                .collect(Collectors.toList());
    }
}
