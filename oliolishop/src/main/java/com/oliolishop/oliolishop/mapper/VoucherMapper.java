package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.voucher.VoucherRequest;
import com.oliolishop.oliolishop.dto.voucher.VoucherResponse;
import com.oliolishop.oliolishop.entity.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface VoucherMapper {

    VoucherResponse response (Voucher voucher);

    Voucher toVoucher (VoucherRequest request);
}
