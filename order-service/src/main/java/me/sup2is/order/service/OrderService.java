package me.sup2is.order.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.order.client.PaymentModuleClient;
import me.sup2is.order.client.ProductServiceClient;
import me.sup2is.order.domain.Order;
import me.sup2is.order.domain.OrderItem;
import me.sup2is.order.domain.dto.ModifyOrderItem;
import me.sup2is.order.domain.dto.ProductStockDto;
import me.sup2is.order.exception.CancelFailureException;
import me.sup2is.order.exception.OrderNotFoundException;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.repository.OrderRepository;
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
            ProductStockDto cachedProductStock = cachedProductStockService.getCachedProductStock(orderItem.getProductId());
            if(cachedProductStock.getStock() < orderItem.getCount()) {
                throw new OutOfStockException("product ID : " + orderItem.getProductId() + " stock is lack");
            }

            orderItem.setPrice(cachedProductStock.getPrice());
        }

        order.setMemberId(memberId);
        order.setTotalPrice();

        paymentModuleClient.payment();
        orderItemService.addItems(order.getOrderItems());
        orderRepository.save(order);
        productServiceClient.modifyStock(ProductStockDto.createDtoByOrderItems(order.getOrderItems()));
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

    public void modify(Long memberId, Long orderId, String newAddress, List<ModifyOrderItem> modifyOrderItems)
            throws OrderNotFoundException, IllegalAccessException, OutOfStockException {

        Order order = findOne(orderId);

        if(order.getMemberId().longValue() != memberId.longValue()) {
            throw new IllegalAccessException("current user is not the owner of this order");
        }

        for (ModifyOrderItem modifyOrderItem : modifyOrderItems) {
            ProductStockDto cachedProductStock = cachedProductStockService.getCachedProductStock(modifyOrderItem.getProductId());
            if(cachedProductStock.getStock() < modifyOrderItem.getCount()) {
                throw new OutOfStockException("product ID : " + modifyOrderItem.getProductId() + " stock is lack");
            }
        }

        order.updateAddress(newAddress);

        for (OrderItem orderItem : order.getOrderItems()) {
            for (ModifyOrderItem modifyOrderItem : modifyOrderItems) {
                if(orderItem.getProductId().longValue() == modifyOrderItem.getProductId().longValue()) {
                    orderItem.modify(modifyOrderItem.getCount());
                }
            }
        }

        order.setTotalPrice();
    }

    public List<Order> findAll(PageRequest orderPageRequest, Long memberId) {
        return orderRepository.findByMemberId(memberId, orderPageRequest);
    }
}

