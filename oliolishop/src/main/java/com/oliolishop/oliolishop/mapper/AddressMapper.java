package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.address.AddressRequest;
import com.oliolishop.oliolishop.dto.address.AddressResponse;
import com.oliolishop.oliolishop.dto.address.AddressUpdateRequest;
import com.oliolishop.oliolishop.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface AddressMapper {

    Address toAddress(AddressRequest request);


    AddressResponse toResponse(Address address);

    Address toAddress(AddressUpdateRequest request);



}
