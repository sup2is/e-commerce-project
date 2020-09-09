package me.sup2is.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.order.domain.audit.AuditTime;
import me.sup2is.order.exception.CancelFailureException;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Long memberId;

    private String address;

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

        order.orderStatus = OrderStatus.ORDER;
        for (OrderItem orderItem : order.orderItems)
            orderItem.setOrder(order);

        order.setTotalPrice();
        return order;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    private void setTotalPrice() {
        this.totalPrice = this.orderItems.stream()
                .map(orderItem -> orderItem.getItemTotalPrice())
                .reduce(0L, Long::sum);
    }

    public void cancel() throws CancelFailureException {
        if (this.orderStatus == OrderStatus.ORDER || this.orderStatus == OrderStatus.CHECK) {
            this.orderStatus = OrderStatus.CANCEL;
            return;
        }

        throw new CancelFailureException(this.getId() + " is already " + this.orderStatus);
    }

    public static class Builder {

        private String address;

        private List<OrderItem> orderItems;

        public Builder orderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        private Order build() {
            Order order = new Order();
            order.orderItems = this.orderItems;
            order.address = this.address;
            return order;
        }
    }
}
