package com.nilemobile.backend.service;

import com.nilemobile.backend.contant.OrderStatus;
import com.nilemobile.backend.exception.Orderexception;
import com.nilemobile.backend.mapper.OrderMapper;
import com.nilemobile.backend.model.*;
import com.nilemobile.backend.repository.AddressRepository;
import com.nilemobile.backend.repository.CartRepository;
import com.nilemobile.backend.reponse.OrderDTO;
import com.nilemobile.backend.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    public OrderServiceImp(CartRepository cartRepository, CartService cartService, ProductService productService, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(User user, Address shippingAddress) {
        return null;
    }

    @Override
    public Order createOrder(User user, Address shippingAddress, List<Long> selectedVariationIds) {

        Logger logger = LoggerFactory.getLogger(OrderServiceImp.class);
        logger.info("Creating order for user: {}", user.getUserId());
        logger.info("ShippingAddress: {}", shippingAddress);
        logger.info("SelectedVariationIds: {}", selectedVariationIds);

        Cart cart = cartRepository.findCartByUserId(user.getUserId());
        if (cart == null) {
            throw new Orderexception("Không tìm thấy giỏ hàng cho user " + user.getUserId());
        }

        List<CartItem> selectedCartItems = cart.getCartItems().stream()
                .filter(item -> selectedVariationIds.contains(item.getVariation().getId()))
                .collect(Collectors.toList());

        if (selectedCartItems.isEmpty()) {
            throw new Orderexception("Không có sản phẩm nào được chọn để tạo đơn hàng!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress); // Có thể là null
        order.setOrderDate(LocalDateTime.now());
        order.setCreateAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.getPaymentDetails().setStatus(PaymentStatus.PENDING);

        long totalPrice = selectedCartItems.stream()
                .mapToLong(item -> item.getVariation().getPrice() * item.getQuantity())
                .sum();
        int totalItem = selectedCartItems.size();
        long totalDiscount = selectedCartItems.stream()
                .mapToLong(item -> (item.getVariation().getPrice() * item.getQuantity() * (item.getVariation().getDiscountPercent() != 0? item.getVariation().getDiscountPercent() : 0)) / 100)
                .sum();

        order.setTotalPrice(totalPrice);
        order.setTotalItem(totalItem);
        order.setTotalDiscountPrice(totalDiscount);
        order.setOrderDetails(cartService.convertCartItemsToOrderDetails(selectedCartItems, order));

        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    @Override
    public Order findOrderById(Long orderId) throws Orderexception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new Orderexception("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    @Override
    public List<Order> orderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }


    @Override
    public Order confirmOrder(Long orderId) throws Orderexception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order= optionalOrder.get();
            if(!order.getStatus().equals(OrderStatus.PLACED.name())){
                throw new Orderexception("Đơn hàng khong thể được xác nhận vì trạng thái của đơn hàng là: "+ order.getStatus());
            }else{
                order.setStatus(OrderStatus.CONFIRMED);
                return orderRepository.save(order);
            }
        }
        throw  new Orderexception("Lỗi");    }

    @Override
    public Order processOrder(Long orderId) throws Orderexception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order= optionalOrder.get();
            if(!order.getStatus().equals(OrderStatus.CONFIRMED.name())){
                throw new Orderexception("Đơn hàng khong thể được xử lý vì trạng thái của đơn hàng là: "+ order.getStatus());
            }else{
                order.setStatus(OrderStatus.PROCESSING);
                return orderRepository.save(order);
            }
        }
        throw  new Orderexception("Lỗi");    }

    @Override
    public Order shippedOrder(Long orderId) throws Orderexception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order= optionalOrder.get();
            if(!order.getStatus().equals(OrderStatus.PROCESSING.name())){
                throw new Orderexception("Đơn hàng khong thể được xử lý vì trạng thái của đơn hàng là: "+ order.getStatus());
            }else{
                order.setStatus(OrderStatus.SHIPPED);
                return orderRepository.save(order);
            }
        }
        throw  new Orderexception("Lỗi");
    }

    @Override
    public Order deliveredOrder(Long orderId) throws Orderexception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order= optionalOrder.get();
            if(!order.getStatus().equals(OrderStatus.SHIPPED.name())){
                throw new Orderexception("Đơn hàng khong thể xác nhận vận chuyển vì trạng thái của đơn hàng là: "+ order.getStatus());
            }else{
                order.setStatus(OrderStatus.DELIVERED);
                return orderRepository.save(order);
            }
        }
        throw  new Orderexception("Lỗi");    }

    @Override
    public Order completeOrder(Long orderId) throws Orderexception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order= optionalOrder.get();
            if(!order.getStatus().equals(OrderStatus.DELIVERED.name())){
                throw new Orderexception("Đơn hàng khong thể xác nhận hoàn thành  vì trạng thái của đơn hàng là: "+ order.getStatus());
            }else{
                order.setStatus(OrderStatus.COMPLETED);
                return orderRepository.save(order);
            }
        }
        throw  new Orderexception("Lỗi");    }

    @Override
    public Order canceledOrder(Long orderId) throws Orderexception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            if(!order.getStatus().equals(OrderStatus.PLACED)){
                throw new Orderexception("Đơn hàng không thể hủy được");
            }
            order.setStatus(OrderStatus.CANCELED);
            return orderRepository.save(order);
        }
        throw new Orderexception("Lỗi");
    }

    @Override
    public List<Order> getAllOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public void deleteOrder(Long orderId) throws Orderexception {

    }

    @Override
    public List<OrderDTO> filterOrderByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new Orderexception("Trạng thái không được để trống");
        }

        List<Order> orders = orderRepository.findByStatus(status);
        return OrderMapper.toDTOs(orders);    }

    @Override
    public Order updateOrderAddress(Long orderId, Address shippingAddress) throws Orderexception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Orderexception("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!order.getStatus().equals(OrderStatus.PLACED)) {
            throw new Orderexception("Không thể cập nhật địa chỉ vì đơn hàng không ở trạng thái PLACED!");
        }

        // Lưu địa chỉ mới nếu nó chưa tồn tại
        if (shippingAddress.getAddressId() == null) {
            shippingAddress = addressRepository.save(shippingAddress);
        }

        order.setShippingAddress(shippingAddress);
        return orderRepository.save(order);
    }
}
