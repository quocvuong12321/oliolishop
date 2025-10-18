package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.mapper.AccountMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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



}
