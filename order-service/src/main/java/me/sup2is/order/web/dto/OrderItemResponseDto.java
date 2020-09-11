package me.sup2is.order.web.dto;

import lombok.Getter;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.OrderStatus;

@Getter
public class OrderItemResponseDto {

    private Long productId;

    private Long price;

    private Integer count;

    private Integer discountRate;

    private OrderStatus orderStatus;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.productId = orderItem.getProductId();
        this.price = orderItem.getPrice();
        this.count = orderItem.getCount();
        this.discountRate = orderItem.getDiscountRate();
        this.orderStatus = orderItem.getOrderStatus();
    }

}
