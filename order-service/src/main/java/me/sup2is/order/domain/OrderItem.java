package me.sup2is.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.order.domain.audit.AuditTime;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends AuditTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private Long price;

    private Integer count;

    private Integer discountRate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public static OrderItem createOrderItem(Builder builder) {
        OrderItem orderItem = new OrderItem();
        orderItem.count = builder.count;
        orderItem.productId = builder.productId;
        orderItem.discountRate = builder.discountRate;
        orderItem.orderStatus = OrderStatus.ORDER;
        return orderItem;
    }

    public void setOrder(Order order){
        this.order = order;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getItemTotalPrice() {
        long totalPrice = price * count;
        if(discountRate != null && discountRate <= 0) return totalPrice;
        long dc = totalPrice / discountRate;
        return totalPrice - dc;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCEL;
    }

    public void modify(Integer count) {
        this.count = count;
    }

    @lombok.Builder
    public static class Builder {

        private Long productId;

        private Integer count;

        private Integer discountRate;

        public OrderItem toEntity() {
            return OrderItem.createOrderItem(this);
        }
    }

}
