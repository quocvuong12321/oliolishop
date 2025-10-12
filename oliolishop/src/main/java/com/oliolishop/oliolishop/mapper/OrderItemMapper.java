package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.order.OrderItemRequest;
import com.oliolishop.oliolishop.dto.order.OrderItemResponse;
import com.oliolishop.oliolishop.entity.Order;
import com.oliolishop.oliolishop.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface OrderItemMapper {
    OrderItem toOrderItem (OrderItemRequest request);

    OrderItemResponse toResponse(OrderItem orderItem);
}
