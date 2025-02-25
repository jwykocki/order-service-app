package com.jw.integration;

import com.jw.constants.OrderProductStatus;
import com.jw.constants.OrderStatus;
import com.jw.dto.request.OrderProductRequest;
import com.jw.dto.request.OrderRequest;
import com.jw.entity.Order;
import com.jw.entity.OrderProduct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@ApplicationScoped
public class DatabaseQueryExecutor {

    private final DataSource dataSource;

    @SneakyThrows
    @Transactional
    public List<Order> returnRowsWithCustomerId(Long customerId) {
        String query =
                "SELECT order_table.orderid, order_table.customerid, order_table.status AS"
                        + " order_status,opt.id AS id, opt.productid, opt.quantity, opt.status AS"
                        + " product_status  FROM order_table JOIN order_product_table opt on"
                        + " order_table.orderid = opt.orderid WHERE order_table.customerid = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Long, Order> orderMap = new HashMap<>();
                while (resultSet.next()) {
                    Long orderId = resultSet.getLong("orderid");
                    Order order =
                            orderMap.computeIfAbsent(
                                    orderId,
                                    id -> {
                                        Order newOrder = new Order();
                                        newOrder.setOrderId(id);
                                        try {
                                            newOrder.setCustomerId(resultSet.getLong("customerid"));
                                            newOrder.setStatus(
                                                    OrderStatus.valueOf(
                                                            resultSet.getString("order_status")));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        newOrder.setOrderProducts(new ArrayList<>());
                                        return newOrder;
                                    });

                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProductId(resultSet.getLong("productid"));
                    orderProduct.setQuantity(resultSet.getInt("quantity"));
                    orderProduct.setStatus(
                            OrderProductStatus.valueOf(resultSet.getString("product_status")));
                    orderProduct.setOrder(order);

                    order.getOrderProducts().add(orderProduct);
                }
                return new ArrayList<>(orderMap.values());
            }
        }
    }

    @SneakyThrows
    @Transactional
    public void saveOrder(OrderRequest orderRequest) {
        String saveOrderQuery =
                "INSERT INTO order_table (orderid, customerid, status) VALUES (?, ?, ?)";
        String saveOrderProductQuery =
                "INSERT INTO order_product_table (id, orderid, productid, quantity, status) VALUES"
                        + " (?, ?, ?, ?, ?)";
        Long orderId = 9999L;
        Long productId = 9999L;

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement orderStatement = connection.prepareStatement(saveOrderQuery)) {
                orderStatement.setLong(1, orderId);
                orderStatement.setLong(2, orderRequest.customerId());
                orderStatement.setString(3, OrderStatus.UNPROCESSED.toString());
                orderStatement.executeUpdate();
            }
            for (OrderProductRequest orderProductRequest : orderRequest.orderProducts()) {
                productId++;
                try (PreparedStatement orderStatement =
                        connection.prepareStatement(saveOrderProductQuery)) {
                    orderStatement.setLong(1, productId);
                    orderStatement.setLong(2, orderId);
                    orderStatement.setLong(3, orderProductRequest.productId());
                    orderStatement.setInt(4, orderProductRequest.quantity());
                    orderStatement.setString(5, String.valueOf(OrderProductStatus.UNKNOWN));
                    orderStatement.executeUpdate();
                }
            }
        }
    }

    @SneakyThrows
    @Transactional
    public void deleteOrder(Long customerId) {
        String selectOrderIdQuery = "SELECT orderid FROM order_table WHERE customerid = ?";
        String deleteOrderQuery = "DELETE FROM order_table WHERE customerid = ?";
        String deleteOrderProductQuery = "DELETE FROM order_product_table WHERE orderid = ?";
        try (Connection connection = dataSource.getConnection()) {

            List<Long> orderIds = new ArrayList<>();
            try (PreparedStatement orderStatement =
                    connection.prepareStatement(selectOrderIdQuery)) {
                orderStatement.setLong(1, customerId);
                try (ResultSet resultSet = orderStatement.executeQuery()) {
                    while (resultSet.next()) {
                        orderIds.add(resultSet.getLong("orderid"));
                    }
                }
            }

            for (Long id : orderIds) {
                try (PreparedStatement orderStatement =
                        connection.prepareStatement(deleteOrderProductQuery)) {
                    orderStatement.setLong(1, id);
                    orderStatement.executeUpdate();
                }
            }

            try (PreparedStatement orderStatement = connection.prepareStatement(deleteOrderQuery)) {
                orderStatement.setLong(1, customerId);
                orderStatement.executeUpdate();
            }
        }
    }

    @SneakyThrows
    @Transactional
    public void deleteAllOrders() {
        String deleteOrderQuery = "DELETE FROM order_table";
        String deleteOrderProductQuery = "DELETE FROM order_product_table";
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement productStatement =
                    connection.prepareStatement(deleteOrderProductQuery)) {
                productStatement.executeUpdate();
            }

            try (PreparedStatement orderStatement = connection.prepareStatement(deleteOrderQuery)) {
                orderStatement.executeUpdate();
            }
        }
    }
}
