package com.nilemobile.backend.service;

import com.nilemobile.backend.exception.ProductException;
import com.nilemobile.backend.model.*;
import com.nilemobile.backend.request.AddCartItemRequest;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {

    public Cart createCart(User user);

//    public String addCartItem(Long userId, AddCartItemRequest addCartItemRequest) throws ProductException;

    public Cart findUserCart(Long userId);


    public void clearCart(Long userId) throws ProductException;

    long calculateTotalPrice(Long cartId);

    int getTotalItems(Long cartId);

    long getTotalDiscount(Long cartId);

    @Transactional
    List<OrderDetail> convertCartToOrderDetails(Cart cart, Order order);

    @Transactional
    List<OrderDetail> convertCartItemsToOrderDetails(List<CartItem> cartItems, Order order);
}
