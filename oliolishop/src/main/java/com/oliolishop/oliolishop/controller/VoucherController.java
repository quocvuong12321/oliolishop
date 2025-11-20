package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.configuration.CheckPermission;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.voucher.VoucherRequest;
import com.oliolishop.oliolishop.dto.voucher.VoucherResponse;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import com.oliolishop.oliolishop.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.Voucher.ROOT)
public class VoucherController {
    @Autowired
    VoucherService voucherService;


    @GetMapping
    public ApiResponse<?> getVoucher( @RequestParam(required = false) String searchKey,
                                      @RequestParam(required = false) VoucherStatus status,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PaginatedResponse<VoucherResponse>>builder()
                .result(
                        voucherService.getVoucher(searchKey,status,page,size)
                )
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<?> getVoucherById(@PathVariable(name = "id")String voucherId){

        return ApiResponse.builder()
                .result(voucherService.getVoucherById(voucherId))
                .build();
    }

    @CheckPermission("VOUCHER_CREATE")
    @PostMapping
    public ApiResponse<VoucherResponse> createVoucher(@Valid @RequestBody VoucherRequest request){

        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.createVoucher(request))
                .build();
    }

    @CheckPermission("VOUCHER_UPDATE")
    @PutMapping(ApiPath.BY_ID)
    public ApiResponse<VoucherResponse> updateVoucher(@Valid @RequestBody VoucherRequest request,@PathVariable String id){

        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.updateVoucher(request,id))
                .build();
    }

    @CheckPermission("VOUCHER_DELETE")
    @DeleteMapping(ApiPath.BY_ID)
    public ApiResponse<String> deleteVoucher(@PathVariable String id){

        voucherService.deleteVoucher(id);
        return ApiResponse.<String>builder()
                .result("")
                .build();
    }


}
