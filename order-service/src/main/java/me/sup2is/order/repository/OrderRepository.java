package me.sup2is.order.repository;

import me.sup2is.order.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberId(Long memberId, Pageable orderPageRequest);
}
