package com.nilemobile.backend.service;

import com.nilemobile.backend.exception.CartException;
import com.nilemobile.backend.exception.ProductException;
import com.nilemobile.backend.model.*;
import com.nilemobile.backend.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImp implements CartService {

    private CartRepository cartRepository;


    private CartItemService cartItemService;

    private ProductService productService;

    private VariationService variationService;

    @Autowired
    public CartServiceImp(CartRepository cartRepository, CartItemService cartItemService, ProductService productService, VariationService variationService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.productService = productService;
        this.variationService = variationService;
    }

    @Transactional
    public Cart getCartById(Long cartId) {
        Cart cart = cartRepository.findByCartIdWithItems(cartId)
                .orElseThrow(() -> new CartException("Cart not found with ID: " + cartId));
        return cart;
    }

    @Transactional
    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartException("Cart not found for user ID: " + userId));
        return cart;
    }

    @Override
    public Cart createCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setSubtotal(0L);
        cart.setTotalItems(0);
        cart.setTotalDiscountPrice(0L);
        return cartRepository.save(cart);
    }

    @Override
    public Cart findUserCart(Long userId) {
        Cart cart = cartRepository.findCartByUserId(userId);
        cart.setTotalItems(cart.getCartItems().size());
        long subtotal = 0;
        long totalDiscountPrice = 0;
        int totalItems = cart.getCartItems().size();

        for (CartItem item : cart.getCartItems()) {
            long itemSubtotal = item.getVariation().getPrice() * item.getQuantity();
            long itemDiscountPrice = (long) (item.getVariation().getPrice() * (item.getVariation().getDiscountPercent() / 100.0)) * item.getQuantity();
            item.setSubtotal(itemSubtotal);
            item.setDiscountPrice(itemDiscountPrice);
            subtotal += itemSubtotal;
            totalDiscountPrice += itemDiscountPrice;
        }

        cart.setSubtotal(subtotal);
        cart.setTotalDiscountPrice(totalDiscountPrice);
        cart.setTotalItems(totalItems);
        cart.setTotalDiscountPercent(5);
        return cart;
    }


    @Override
    @Transactional
    public void clearCart(Long userId) throws ProductException {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        Cart cart = cartRepository.findCartByUserId(userId);
        if (cart == null) {
            throw new CartException("Cart not found for user ID: " + userId);
        }
        cart.getCartItems().clear();
        cart.setSubtotal(0L);
        cart.setTotalItems(0);
        cart.setTotalDiscountPrice(0L);
        cartRepository.save(cart);
    }

    @Override
    public long calculateTotalPrice(Long cartId) {
        Cart cart = getCartById(cartId);
        return cart.getSubtotal();
    }

    @Override
    public int getTotalItems(Long cartId) {
        Cart cart = getCartById(cartId);
        return cart.getTotalItems();
    }

    @Override
    public long getTotalDiscount(Long cartId){
        Cart cart = getCartById(cartId);
        return cart.getTotalDiscountPrice();
    }
    @Transactional
    @Override
    public List<OrderDetail> convertCartToOrderDetails(Cart cart, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setVariation(cartItem.getVariation());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

    @Transactional
    @Override
    public List<OrderDetail> convertCartItemsToOrderDetails(List<CartItem> cartItems, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setVariation(cartItem.getVariation());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }
}
