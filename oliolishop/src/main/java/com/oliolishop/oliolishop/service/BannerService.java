package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.banner.BannerRequest;
import com.oliolishop.oliolishop.dto.banner.BannerResponse;
import com.oliolishop.oliolishop.entity.Banner;
import com.oliolishop.oliolishop.entity.Category;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.BannerMapper;
import com.oliolishop.oliolishop.mapper.CategoryMapper;
import com.oliolishop.oliolishop.repository.BannerRepository;
import com.oliolishop.oliolishop.util.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class BannerService {
    private final CategoryMapper categoryMapper;

    private final BannerMapper bannerMapper;
    private final BannerRepository bannerRepository;

    @Transactional
    public BannerResponse createBanner(BannerRequest request, MultipartFile file,String imageDir,String folder){
        Banner banner = bannerMapper.toBanner(request);

        banner.setCategory(Category.builder()
                        .id(request.getCategoryId())
                .build());

        banner.setId(UUID.randomUUID().toString());

        String imageUrl = saveImage(banner.getId(),file,imageDir,folder);

        banner.setImage(imageUrl);

        return bannerMapper.toResponse(bannerRepository.save(banner));


    }

    private static String saveImage(String bannerId, MultipartFile file, String imageDir,String folder) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 1. Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(imageDir,folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Tạo tên file duy nhất (để tránh trùng lặp)
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // Tên file: bannerId + đuôi mở rộng
            String uniqueFileName = bannerId + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            // 3. Sao chép byte gốc của file (giữ nguyên chất lượng và kích thước)
            // Phương thức transferTo hoặc Files.copy sẽ sao chép dữ liệu byte gốc.
            Files.copy(file.getInputStream(), filePath);

            // 4. Trả về đường dẫn để lưu vào database
            // Trong môi trường production, đây sẽ là một URL S3 hoặc CDN.
            return folder+"/"+uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteBanner(String bannerId,String imageDir){
        Banner banner = bannerRepository.findById(bannerId).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_EXIST));

        // Xóa file ảnh nếu tồn tại
        if (banner.getImage() != null && !banner.getImage().isEmpty()) {
            Path imagePath = Paths.get(imageDir, banner.getImage()); // imageBaseDir là folder gốc lưu ảnh
            try {
                Files.deleteIfExists(imagePath); // xóa file nếu tồn tại
            } catch (IOException e) {
                // Log lỗi nhưng không chặn việc xóa banner
                System.out.println("Failed to delete banner image: "+e);
            }
        }

        bannerRepository.delete(banner);
    }

    public PaginatedResponse<BannerResponse> getBanners(String name, String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));

        Specification<Banner> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId));
        }

        Page<Banner> banners = bannerRepository.findAll(spec, pageable);



        return PaginatedResponse.fromSpringPage(banners.map(bannerMapper::toResponse));
    }

    public BannerResponse getBannerById(String bannerId){

        return bannerMapper.toResponse(bannerRepository.findById(bannerId)
                .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_EXIST)));

    }

    @Transactional
    public BannerResponse updateBanner(String bannerId, BannerRequest request, MultipartFile file, String imageDir, String folder) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_EXIST));


        // 1. Cập nhật thông tin cơ bản
        banner.setName(request.getName());
        banner.setCategory(Category.builder().id(request.getCategoryId()).build());
        banner.setContent(request.getContent()); // nếu có

        // 2. Nếu có file mới, xóa ảnh cũ và lưu ảnh mới
        if (file != null && !file.isEmpty()) {
            // Xóa file cũ nếu tồn tại
            if (banner.getImage() != null && !banner.getImage().isEmpty()) {
                Path oldImagePath = Paths.get(imageDir, banner.getImage());
                try {
                    Files.deleteIfExists(oldImagePath);
                } catch (IOException e) {
                    log.warn("Failed to delete old banner image: {}", oldImagePath, e);
                }
            }

            // Lưu file mới
            String imageUrl = saveImage(banner.getId(), file, imageDir, folder);
            banner.setImage(imageUrl);
        }

        // 3. Lưu và trả về response
        Banner updated = bannerRepository.save(banner);
        BannerResponse response = bannerMapper.toResponse(updated);
        return response;

    }
}
