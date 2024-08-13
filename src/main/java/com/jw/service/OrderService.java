package com.jw.service;

import com.jw.dto.OrderRequest;
import com.jw.dto.OrderResponse;
import com.jw.dto.reservation.ProductReservationRequest;
import com.jw.dto.reservation.ReservationResult;
import com.jw.entity.Order;
import com.jw.entity.OrderStatus;
import com.jw.error.OrderNotFoundException;
import com.jw.error.ReservationFailException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.InternalServerErrorException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ReservationService reservationService;

    @Transactional
    public OrderResponse processOrderRequest(OrderRequest orderRequest) {
        log.info("Processing order {}", orderRequest);
        Order order = createOrderInDatabase(orderRequest);
        OrderStatus reservationStatus =
                processReservationRequest(orderMapper.toProductReservationRequest(order));
        updateOrderStatus(order, reservationStatus);
        return orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.listAll().stream().map(orderMapper::toOrderResponse).toList();
    }

    @Transactional
    public void deleteOrder(Long id) {
        checkIfOrderExistsOrElseThrowException(id);
        orderRepository.deleteById(id);
    }

    public OrderResponse getOrderById(Long id) {
        checkIfOrderExistsOrElseThrowException(id);
        Order order = orderRepository.findById(id);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse processUpdateOrder(Long orderId, OrderRequest orderRequest) {
        checkIfOrderExistsOrElseThrowException(orderId);
        Order order = orderMapper.toOrder(orderRequest);
        order.setOrderId(orderId);
        String status = orderRepository.findById(orderId).getStatus();
        order.setStatus(status);
        updateOrder(order);
        return orderMapper.toOrderResponse(order);
    }

    private Order updateOrder(Order order) {
        orderRepository.getEntityManager().merge(order);
        return order;
    }

    private Order createOrderInDatabase(OrderRequest orderRequest) {
        Order order = orderMapper.toOrder(orderRequest);
        setOrderStatus(order, OrderStatus.UNCOMPLETED);
        orderRepository.persist(order);
        return order;
    }

    private void setOrderStatus(Order order, OrderStatus orderStatus) {
        order.setStatus(orderStatus.toString());
    }

    private OrderStatus processReservationRequest(
            ProductReservationRequest productReservationRequest) {
        ReservationResult result =
                reservationService.sendReservationRequest(productReservationRequest);
        log.info("Received reservation result {}", result);
        return getReservationStatus(result.status());
    }

    private OrderStatus getReservationStatus(String status) {
        return switch (status) {
            case "SUCCESS" -> OrderStatus.RESERVED;
            case "FAIL" -> throw new ReservationFailException("Reservation could not be processed");
            default -> throw new InternalServerErrorException("Unexpected reservation status");
        };
    }

    private Order updateOrderStatus(Order order, OrderStatus status) {
        setOrderStatus(order, status);
        return updateOrder(order);
    }

    private void checkIfOrderExistsOrElseThrowException(Long id) {
        orderRepository
                .findByIdOptional(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}
