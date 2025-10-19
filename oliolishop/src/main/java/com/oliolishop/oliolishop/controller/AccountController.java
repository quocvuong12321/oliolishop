package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.service.AccountService;
import com.oliolishop.oliolishop.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE+ApiPath.ACCOUNT)
public class AccountController {
    @Autowired
    AccountService accountService;

    


}
