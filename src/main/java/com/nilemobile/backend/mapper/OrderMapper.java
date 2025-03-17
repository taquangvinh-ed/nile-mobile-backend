package com.nilemobile.backend.mapper;

import com.nilemobile.backend.model.Address;
import com.nilemobile.backend.model.Order;
import com.nilemobile.backend.model.OrderDetail;
import com.nilemobile.backend.reponse.AddressDTO;
import com.nilemobile.backend.reponse.OrderDetailDTO;
import com.nilemobile.backend.reponse.OrderDTO;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setTotalDiscountPrice(order.getTotalDiscountPrice());
        dto.setTotalItem(order.getTotalItem());
        dto.setStatus(order.getStatus().name());
        dto.setUserId(order.getUser().getUserId());
            dto.setShippingAddress(toAddressDTO(order.getShippingAddress()));
        dto.setOrderDetails(toOrderDetailDTOs(order.getOrderDetails()));
        return dto;
    }

    public static List<OrderDTO> toDTOs(List<Order> orders) {
        return orders.stream().map(OrderMapper::toDTO).collect(Collectors.toList());
    }

    private static AddressDTO toAddressDTO(Address address) {
        if (address == null) return null;

        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setFirstName(address.getFirstName());
        dto.setLastName(address.getLastName());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setAddressLine(address.getAddressLine());
        dto.setWard(address.getWard());
        dto.setDistrict(address.getDistrict());
        dto.setProvince(address.getProvince());
        return dto;
    }

    private static List<OrderDetailDTO> toOrderDetailDTOs(List<OrderDetail> orderDetails) {
        if (orderDetails == null) return null;

        return orderDetails.stream().map(detail -> {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setId(detail.getId());
            dto.setVariationId(detail.getVariation().getId());
            dto.setQuantity(detail.getQuantity());
            dto.setSubtotal(detail.getSubtotal());
            return dto;
        }).collect(Collectors.toList());
    }
}
