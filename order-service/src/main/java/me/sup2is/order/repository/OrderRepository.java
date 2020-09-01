package me.sup2is.order.repository;

import me.sup2is.order.domain.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
