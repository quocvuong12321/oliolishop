package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.order.OrderRequest;
import com.oliolishop.oliolishop.dto.order.OrderResponse;
import com.oliolishop.oliolishop.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface OrderMapper {

    Order toOrder(OrderRequest request);

    OrderResponse toResponse(Order order);

}
