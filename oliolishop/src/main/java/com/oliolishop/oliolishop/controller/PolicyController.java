package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.shop_policy.PolicyRequest;
import com.oliolishop.oliolishop.dto.shop_policy.PolicyResponse;
import com.oliolishop.oliolishop.entity.ShopPolicyPdf;
import com.oliolishop.oliolishop.repository.RoleRepository;
import com.oliolishop.oliolishop.service.PolicyService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ApiPath.Policy.ROOT)
public class PolicyController {
    @Autowired
    PolicyService policyService;

    @Value("${app.image-dir}")
    String baseDir;

    @Autowired
    private RoleRepository roleRepository;


    @GetMapping
    public ApiResponse<List<PolicyResponse>> getAll(){
        return ApiResponse.<List<PolicyResponse>>builder()
                .result(policyService.getAll())
                .build();
    }


    @GetMapping( ApiPath.BY_ID+ApiPath.Policy.PDF)
    public ResponseEntity<byte[]> loadPdf(@PathVariable(name = "id") String item) throws IOException {
        byte[] data = policyService.loadPdfBytes(item,baseDir);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @PostMapping(ApiPath.Policy.CREATE)
    public ApiResponse<ShopPolicyPdf> createPolicy(@RequestPart("request")PolicyRequest request, @RequestPart("pdfFile") MultipartFile pdfFile){

        return ApiResponse.<ShopPolicyPdf>builder()
                .result(policyService.create(request,pdfFile,baseDir,ApiPath.FOLDER_PDF_POLICIES))
                .build();

    }

    @PutMapping(ApiPath.BY_ID+ApiPath.Policy.UPDATE)
    public ApiResponse<ShopPolicyPdf> updatePolicy(@PathVariable(name = "id") Long id, @RequestParam("pdfFile") MultipartFile pdfFile){


        return ApiResponse.<ShopPolicyPdf>builder()
                .result(policyService.update(id,pdfFile,baseDir,ApiPath.FOLDER_PDF_POLICIES))
                .build();
    }

    @DeleteMapping(ApiPath.BY_ID + ApiPath.Policy.DELETE)
    public ApiResponse<String> deletePolicy(@PathVariable Long id) throws IOException {

        policyService.delete(id);

        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS,"Xóa chính sách"))
                .build();
    }





}
