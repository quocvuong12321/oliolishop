package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.address.AddressRequest;
import com.oliolishop.oliolishop.dto.address.AddressResponse;
import com.oliolishop.oliolishop.dto.address.AddressUpdateRequest;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.Customer.ROOT)
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @GetMapping(ApiPath.Customer.ADDRESS)
    public ApiResponse<List<AddressResponse>> getAddress() {

        return ApiResponse.<List<AddressResponse>>builder()
                .result(customerService.getAddresses())
                .build();
    }

    @PostMapping(ApiPath.Customer.ADDRESS)
    public ApiResponse<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {

        return ApiResponse.<AddressResponse>builder()
                .result(customerService.createAddress(request))
                .build();
    }

    @PutMapping(ApiPath.Customer.ADDRESS+ApiPath.BY_ID)
    public ApiResponse<AddressResponse> updateAddress(@PathVariable(value = "id")String addressId , @Valid @RequestBody AddressUpdateRequest request){
        return ApiResponse.<AddressResponse>builder()
                .result(customerService.updateAddress(request,addressId))
                .build();
    }

    @DeleteMapping(ApiPath.Customer.ADDRESS+ApiPath.BY_ID)
    public ApiResponse<String> deleteAddress(@PathVariable("id")String addressId){
        customerService.deleteAddress(addressId);
        return ApiResponse.<String>builder()
                .result("")
                .build();
    }
}
