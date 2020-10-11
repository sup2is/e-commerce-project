package me.sup2is.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
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
        Order order = new Order();
        order.orderItems = builder.orderItems;
        order.address = builder.address;

        order.orderStatus = OrderStatus.ORDER;
        for (OrderItem orderItem : order.orderItems)
            orderItem.setOrder(order);

        return order;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setTotalPrice() {
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

    public void updateAddress(String newAddress) throws IllegalAccessException {
        if(!this.orderStatus.equals(OrderStatus.ORDER)){
            throw new IllegalAccessException("this order is already " + orderStatus.name());
        }
        this.address = newAddress;
    }

    public void checkOwner(Long memberId) throws IllegalAccessException {
        if(this.memberId != memberId.longValue()){
            throw new IllegalAccessException("current user is not the owner of this order");
        }
    }

    @lombok.Builder
    public static class Builder {

        private String address;

        private List<OrderItem> orderItems;

        public Order toEntity() {
            return Order.createOrder(this);
        }

    }
}
