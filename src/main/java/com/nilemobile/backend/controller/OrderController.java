package com.nilemobile.backend.controller;

import com.nilemobile.backend.exception.Orderexception;
import com.nilemobile.backend.mapper.OrderMapper;
import com.nilemobile.backend.model.Address;
import com.nilemobile.backend.model.Order;
import com.nilemobile.backend.model.User;
import com.nilemobile.backend.reponse.OrderDTO;
import com.nilemobile.backend.service.OrderService;
import com.nilemobile.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/user/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestParam Long userId, @RequestBody(required = false) Address shippingAddress) {
        Logger logger = LoggerFactory.getLogger(OrderController.class);
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Order order = orderService.createOrder(user, shippingAddress);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            if (orderDTO == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.findOrderById(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{userId}/orders/history")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(@PathVariable Long userId) {
        List<Order> orders = orderService.orderHistory(userId);
        List<OrderDTO> orderDTOs = OrderMapper.toDTOs(orders);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/user/{userId}/orders/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getAllOrders(userId);
        List<OrderDTO> orderDTOs = OrderMapper.toDTOs(orders);
        return ResponseEntity.ok(orderDTOs);
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.confirmOrder(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{orderId}/update-address")
    public ResponseEntity<OrderDTO> updateOrderAddress(
            @PathVariable Long orderId,
            @RequestBody Address shippingAddress) {
        try {
            Order order = orderService.updateOrderAddress(orderId, shippingAddress);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{orderId}/process")
    public ResponseEntity<OrderDTO> processOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.processOrder(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<OrderDTO> shipOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.shippedOrder(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<OrderDTO> deliverOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.deliveredOrder(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<OrderDTO> completeOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.completeOrder(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.canceledOrder(orderId);
            OrderDTO orderDTO = OrderMapper.toDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Xóa đơn hàng thành công!");
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OrderDTO>> filterOrdersByStatus(@RequestParam String status) {
        try {
            List<OrderDTO> orderDTOs = orderService.filterOrderByStatus(status);
            return ResponseEntity.ok(orderDTOs);
        } catch (Orderexception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
