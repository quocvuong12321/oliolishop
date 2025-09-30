package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface CustomerMapper {
    @Mapping(source = "id", target = "customerId")
    CustomerResponse toResponse(Customer customer);

    Customer toCustomer(CustomerRequest request);

}
