package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.address.AddressRequest;
import com.oliolishop.oliolishop.dto.address.AddressResponse;
import com.oliolishop.oliolishop.dto.address.AddressUpdateRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Address;
import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.entity.Ward;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.AddressMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.AddressRepository;
import com.oliolishop.oliolishop.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
   CustomerMapper customerMapper;
    AccountRepository accountRepository;

    AddressRepository addressRepository;
    private final AddressMapper addressMapper;




    public CustomerResponse createCustomer(CustomerRequest request,String accountId){
        Account c = accountRepository.findById(accountId).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Customer customer = customerMapper.toCustomer(request);

        String id = UUID.randomUUID().toString();
        customer.setAccount(c);
        customer.setId(id);


        return customerMapper.toResponse(customerRepository.save(customer));
    }

    public List<AddressResponse> getAddresses(String customerId){
        List<Address> addresses = addressRepository.findByCustomerId(customerId).orElse(new ArrayList<>());

        return addresses.stream().map(addressMapper::toResponse).toList();
    }

    public AddressResponse createAddress(AddressRequest request){

        Address newAddress = addressMapper.toAddress(request);

        newAddress.setCustomer(Customer.builder()
                .id(request.getCustomerId())
                .build()); //Chỉ cần ấy id thôi vì đây là reference ảo nên khi lưu nó chỉ lấy customer id là đủ

        newAddress.setWard(Ward.builder()
                .id(request.getWardId()).build());

        return addressMapper.toResponse(addressRepository.save(newAddress));
    }

    public AddressResponse updateAddress(AddressUpdateRequest request,String addressId){
        Address address = addressRepository.findById(addressId).orElseThrow(()->new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        address.setDetailAddress(request.getDetailAddress());
        address.setIdDefault(request.getIsDefault());
        address.setName(request.getName());
        address.setWard(Ward.builder().id(request.getWardId()).build());
        address.setPhoneNumber(request.getPhoneNumber());

        return addressMapper.toResponse(addressRepository.save(address));
    }

    public void deleteAddress(String addressId){
        Address address = addressRepository.findById(addressId).orElseThrow(()->new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        addressRepository.delete(address);
    }
}
