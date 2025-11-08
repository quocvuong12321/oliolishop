package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    public PaginatedResponse<VoucherResponse> getVoucher(String searchKey, VoucherStatus status, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Query kết quả
        Page<VoucherResponse> resultPage;

        if (searchKey != null && !searchKey.isBlank()) {
            // --- Có searchKey ---
            if (status != null) {
                // Có cả status
                resultPage = voucherRepository
                        .findByNameContainingIgnoreCaseAndStatus(searchKey, status, pageable)
                        .map(voucher -> {
                            VoucherResponse response = voucherMapper.response(voucher);
                            response.setStatus(voucher.getStatus());
                            return response;
                        });
            } else {
                // Không có status → findByName thôi
                resultPage = voucherRepository
                        .findByNameContainingIgnoreCase(searchKey, pageable)
                        .map(voucher -> {
                            VoucherResponse response = voucherMapper.response(voucher);
                            response.setStatus(voucher.getStatus());
                            return response;
                        });
            }
        } else {
            // --- Không có searchKey ---
            if (status != null) {
                // Chỉ lọc theo status
                resultPage = voucherRepository
                        .findByStatus(status, pageable)
                        .map(voucher -> {
                            VoucherResponse response = voucherMapper.response(voucher);
                            response.setStatus(voucher.getStatus());
                            return response;
                        });
            } else {
                // Không có status → lấy toàn bộ
                resultPage = voucherRepository
                        .findAll(pageable)
                        .map(voucher -> {
                            VoucherResponse response = voucherMapper.response(voucher);
                            response.setStatus(voucher.getStatus());
                            return response;
                        });
            }
        }

        // 3. Trả về dạng PaginatedResponse
        return PaginatedResponse.fromSpringPage(resultPage);
    }


    public VoucherResponse createVoucher(VoucherRequest request) {
        Voucher voucher = voucherMapper.toVoucher(request);

        voucher.setStatus(VoucherStatus.Active);
        voucher.setId(UUID.randomUUID().toString());

        Voucher saved = voucherRepository.save(voucher);
        VoucherResponse response = voucherMapper.response(saved);
        response.setStatus(saved.getStatus());
        return response;
    }

    public VoucherResponse updateVoucher(VoucherRequest request, String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));

        voucher.setName(request.getName());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setAmount(request.getAmount());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscountValue(request.getMaxDiscountValue());
        voucher.setMinOrderValue(request.getMinOrderValue());
        if(voucher.getStartDate().isBefore(LocalDateTime.now()) && voucher.getEndDate().isAfter(LocalDateTime.now()))
            voucher.setStatus(VoucherStatus.Active);
        Voucher updated = voucherRepository.save(voucher);
        VoucherResponse response = voucherMapper.response(updated);
        response.setStatus(updated.getStatus());

        return response;
    }

    public void deleteVoucher(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));
        voucher.setStatus(VoucherStatus.Inactive);
        voucherRepository.save(voucher);
    }

    public VoucherResponse getVoucherById(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));

        VoucherResponse response = voucherMapper.response(voucher);
        response.setStatus(voucher.getStatus());
        return response;
    }

}
