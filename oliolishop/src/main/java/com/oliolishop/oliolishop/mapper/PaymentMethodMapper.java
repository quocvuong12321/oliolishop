package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.payment.PaymentMethodResponse;
import com.oliolishop.oliolishop.entity.PaymentMethod;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface PaymentMethodMapper {

    PaymentMethodResponse toResponse (PaymentMethod paymentMethod);

}
