package me.sup2is.order.repository;

import me.sup2is.order.domain.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
