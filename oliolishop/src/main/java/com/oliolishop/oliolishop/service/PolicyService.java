package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.shop_policy.PolicyRequest;
import com.oliolishop.oliolishop.dto.shop_policy.PolicyResponse;
import com.oliolishop.oliolishop.entity.ShopPolicyPdf;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.ShopPolicyPdfRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PolicyService {

    ShopPolicyPdfRepository shopPolicyRepository;


    @CacheEvict(value = "policyList", allEntries = true)
    public ShopPolicyPdf create(PolicyRequest request, MultipartFile pdfFile, String baseDir, String folder) {

        try {
            if (shopPolicyRepository.existsByItem(request.getItem())) {
                throw new AppException(ErrorCode.POLICY_EXISTED);
            }

            // Lưu file PDF và trả về path lưu DB
            String pdfPath = saveFile(request.getItem(), pdfFile, baseDir, folder);

            ShopPolicyPdf entity = new ShopPolicyPdf();
            entity.setItem(request.getItem());
            entity.setName(request.getName());
            entity.setPdfPath(pdfPath);

            return shopPolicyRepository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("Failed to store PDF", e);
        }
    }

    private static String saveFile(String fileId, MultipartFile file, String baseDir, String folder) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 1. Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(baseDir, folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Lấy đuôi file
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 3. Tạo tên file duy nhất
            String uniqueFileName = fileId + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            // 4. Copy file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 5. Trả về path tương đối lưu DB
            return folder + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public File loadPdf(String item, String baseDir) {
        ShopPolicyPdf policy = shopPolicyRepository.findByItem(item)
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_EXIST));

        // Kết hợp baseDir + path tương đối lưu DB
        Path fullPath = Paths.get(baseDir, policy.getPdfPath());
        File file = fullPath.toFile();

        if (!file.exists()) {
            throw new AppException(ErrorCode.PDF_NOT_FOUND);
        }

        return file;
    }

    public byte[] loadPdfBytes(String item,String baseDir) throws IOException {
        File file = loadPdf(item,baseDir);
        return Files.readAllBytes(file.toPath());
    }

    @CacheEvict(value = "policyList", allEntries = true)
    public ShopPolicyPdf update(Long id, MultipartFile pdfFile, String baseDir, String folder) {
        try {
            ShopPolicyPdf entity = shopPolicyRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_EXIST));

            // Xóa file cũ nếu có
            if (entity.getPdfPath() != null) {
                Path oldFile = Paths.get(baseDir, entity.getPdfPath());
                Files.deleteIfExists(oldFile);
            }

            // Lưu PDF mới
            String pdfPath = saveFile(entity.getItem(), pdfFile, baseDir, folder);
            entity.setPdfPath(pdfPath);

            return shopPolicyRepository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update PDF", e);
        }
    }


    @CacheEvict(value = "policyList", allEntries = true)
    public void delete(Long id) throws IOException {
        ShopPolicyPdf entity = shopPolicyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_EXIST));


        if (entity.getPdfPath() != null) {
            Files.deleteIfExists(Paths.get(entity.getPdfPath()));


            // Xóa record trong DB
            shopPolicyRepository.delete(entity);
        }
    }


    @Cacheable(value = "policyList")
    public List<PolicyResponse> getAll(){

        return shopPolicyRepository.findAll().stream().map(p->
                {
                    return PolicyResponse.builder()
                            .name(p.getName())
                            .id(p.getId())
                            .item(p.getItem())
                    .build();
                }
        ).toList();


    }
}
