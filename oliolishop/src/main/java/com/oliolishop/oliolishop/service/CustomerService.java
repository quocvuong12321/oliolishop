package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AccountRepository accountRepository;

    public CustomerResponse createCustomer(CustomerRequest request,String accountId){
        Account c = accountRepository.findById(accountId).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Customer customer = customerMapper.toCustomer(request);

        String id = UUID.randomUUID().toString();
        customer.setAccount(c);
        customer.setId(id);


        return customerMapper.toResponse(customerRepository.save(customer));
    }


}
