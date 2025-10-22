package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.voucher.VoucherRequest;
import com.oliolishop.oliolishop.dto.voucher.VoucherResponse;
import com.oliolishop.oliolishop.entity.Voucher;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.VoucherMapper;
import com.oliolishop.oliolishop.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    public Page<VoucherResponse> getVoucher(int size, int page){
        Pageable pageable =  PageRequest.of(page,size);

        return voucherRepository.findAll(pageable).map(voucherMapper::response);

    }

    public VoucherResponse createVoucher(VoucherRequest request){

        Voucher voucher = voucherMapper.toVoucher(request);

        voucher.setStatus(VoucherStatus.Active);

        voucher.setId(UUID.randomUUID().toString());


        return voucherMapper.response(voucherRepository.save(voucher));

    }

    public VoucherResponse updateVoucher(VoucherRequest request,String voucherId){
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));

        voucher.setName(request.getName());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setAmount(request.getAmount());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscountValue(request.getMaxDiscountValue());
        voucher.setMinOrderValue(request.getMinOrderValue());

        return voucherMapper.response(voucherRepository.save(voucher));
    }

    public void deleteVoucher(String voucherId){
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));
        voucher.setStatus(VoucherStatus.Inactive);

        voucherRepository.save(voucher);
    }

}
