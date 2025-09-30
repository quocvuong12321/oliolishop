package com.oliolishop.oliolishop.service;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.entity.Permission;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.AccountMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.extensions.compactnotation.PackageCompactConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AccountService {


    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    CustomerService customerService;
    private final CustomerMapper customerMapper;

    public AccountResponse createAccount(AccountRequest request){
        boolean existedUsername = accountRepository.existsByUsername(request.getUsername());
        if(existedUsername){
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }
        boolean existedEmail = accountRepository.existsByEmail(request.getEmail());
        if(existedEmail){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }


        Account account = accountMapper.toAccount(request);
        String id = UUID.randomUUID().toString();
        String password = passwordEncoder.encode(request.getPassword());
        account.setId(id);
        account.setPassword(password);
        CustomerRequest customer = request.getCustomerRequest();

        AccountResponse result = accountMapper.toAccountResponse(accountRepository.save(account));

        result.setCustomerResponse(customerService.createCustomer(customer,id));

        return result;
    }

}
