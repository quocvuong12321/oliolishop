package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface AccountMapper {

    AccountResponse toAccountResponse(Account account);

//    @Mapping(ignore = true,target = "password")
    Account toAccount(AccountRequest request);

}
