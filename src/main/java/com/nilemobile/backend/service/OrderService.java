package com.nilemobile.backend.service;

import com.nilemobile.backend.exception.Orderexception;
import com.nilemobile.backend.model.Address;
import com.nilemobile.backend.model.Order;
import com.nilemobile.backend.model.User;
import com.nilemobile.backend.reponse.OrderDTO;

import java.util.List;

public interface OrderService {

    public Order createOrder(User user, Address shippingAddress);

    Order createOrder(User user, Address shippingAddress, List<Long> selectedVariationIds);

    public Order findOrderById(Long orderId) throws Orderexception;

    public List<Order> orderHistory(Long userId);


    public Order confirmOrder(Long orderId) throws Orderexception;

    Order processOrder(Long orderId) throws Orderexception;

    public Order shippedOrder(Long orderId) throws Orderexception;

    public Order deliveredOrder(Long orderId) throws Orderexception;

    Order completeOrder(Long orderId) throws Orderexception;

    public Order canceledOrder(Long orderId) throws Orderexception;

    public List<Order> getAllOrders(Long userId);

    public void deleteOrder(Long orderId) throws Orderexception;

    public List<OrderDTO> filterOrderByStatus(String status);

    Order updateOrderAddress(Long orderId, Address shippingAddress) throws Orderexception;
}
