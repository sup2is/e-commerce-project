package me.sup2is.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.order.domain.audit.AuditTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends AuditTime{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long id;

    private Long memberId;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Override
    public LocalDateTime getCreateAt() {
        return super.getCreateAt();
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return super.getUpdatedAt();
    }

    public static Order createOrder(Builder builder) {
        Order order = builder.build();

        order.orderStatus = OrderStatus.CHECK;
        for (OrderItem orderItem : order.orderItems)
            orderItem.setOrder(order);

        order.setTotalPrice();
        return order;
    }

    private void setTotalPrice() {
        this.totalPrice = this.orderItems.stream()
                .map(orderItem -> orderItem.getPrice() * orderItem.getCount())
                .reduce(0L, Long::sum);
    }

    public static class Builder {

        private Long memberId;

        private List<OrderItem> orderItems;

        public Builder memberId(Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder orderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        private Order build() {
            Order order = new Order();
            order.memberId = this.memberId;
            order.orderItems = this.orderItems;
            return order;
        }
    }
}
