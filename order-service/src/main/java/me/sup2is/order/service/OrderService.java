package me.sup2is.order.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.order.client.PaymentModuleClient;
import me.sup2is.order.client.ProductServiceClient;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.CancelFailureException;
import me.sup2is.order.exception.OrderNotFoundException;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.repository.OrderRepository;
import me.sup2is.order.web.dto.ProductStockModifyRequestDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CachedProductStockService cachedProductStockService;
    private final PaymentModuleClient paymentModuleClient;
    private final ProductServiceClient productServiceClient;

    public void order(Long memberId, Order order) throws OutOfStockException {
        for (OrderItem orderItem : order.getOrderItems()) {
            cachedProductStockService.checkItemStock(orderItem.getProductId(), orderItem.getCount());
            ProductStockDto cachedProductStock = cachedProductStockService.getCachedProductStock(orderItem.getProductId());
            orderItem.setPrice(cachedProductStock.getPrice());
        }

        order.setMemberId(memberId);
        order.setTotalPrice();

//        paymentModuleClient.payment(); //결제모듈 미구현
        orderItemService.addItems(order.getOrderItems());
        orderRepository.save(order);

        //todo message 기반으로 변경
        productServiceClient.modifyStock(ProductStockModifyRequestDto.createDtoByOrderItems(order.getOrderItems()));
    }

    public Order findOne(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("order is not found"));
    }

    public void cancel(Long orderId, Long memberId) throws CancelFailureException, OrderNotFoundException, IllegalAccessException {
        Order order = findOne(orderId);

        if(order.getMemberId().longValue() != memberId.longValue()) {
            throw new IllegalAccessException("current user is not the owner of this order");
        }

        order.cancel();
        orderItemService.cancelItems(order.getOrderItems());
    }

    public void modify(Long memberId, Long orderId, String newAddress)
            throws OrderNotFoundException, IllegalAccessException, OutOfStockException {
        Order order = findOne(orderId);
        order.checkOwner(memberId);
        order.updateAddress(newAddress);
    }

    public List<Order> findAll(PageRequest orderPageRequest, Long memberId) {
        return orderRepository.findByMemberId(memberId, orderPageRequest);
    }
}

