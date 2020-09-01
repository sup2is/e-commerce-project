package me.sup2is.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.order.domain.audit.AuditTime;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends AuditTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private Long price;

    private Integer count;

    private Integer discountRate;

    public static OrderItem createOrderItem(Builder builder) {
        return builder.build();
    }

    public void setOrder(Order order){
        this.order = order;
    }

    public Long getItemTotalPrice() {
        long totalPrice = price * count;
        if(discountRate != null && discountRate <= 0) return totalPrice;
        long dc = totalPrice / discountRate;
        return totalPrice - dc;
    }

    public static class Builder {

        private Long productId;

        private Long price;

        private Integer count;

        private Integer discountRate;

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder price(Long price) {
            this.price = price;
            return this;
        }

        public Builder count(Integer count) {
            this.count = count;
            return this;
        }

        public Builder discountRate(Integer discountRate) {
            this.discountRate = discountRate;
            return this;
        }

        private OrderItem build() {
            OrderItem orderItem = new OrderItem();
            orderItem.count = this.count;
            orderItem.productId = this.productId;
            orderItem.price = this.price;
            orderItem.discountRate = this.discountRate;
            return orderItem;
        }

    }

}
