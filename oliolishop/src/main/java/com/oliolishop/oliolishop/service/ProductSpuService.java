package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.BreadCrumbResponse;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrRequest;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrResponse;
import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductDetailResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateRequest;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.dto.productspu.SpuCreateResponse;
import com.oliolishop.oliolishop.entity.Brand;
import com.oliolishop.oliolishop.entity.Category;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSpu;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.*;
import com.oliolishop.oliolishop.repository.*;
import com.oliolishop.oliolishop.ultils.AppUtils;
import jakarta.persistence.GeneratedValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@RequiredArgsConstructor
public class ProductSpuService {
    private final DescriptionAttrRepository descriptionAttrRepository;
    BrandRepository brandRepository;
    CategoryRepository categoryRepository;
    ProductSpuMapper productSpuMapper;
    BrandMapper brandMapper;
    CategoryService categoryService;
    CategoryMapper categoryMapper;
    ProductSpuRepository productSpuRepository;
    ProductSkuAttrMapper productSkuAttrMapper;
    DescriptionAttrMapper descriptionAttrMapper;
    DescriptionAttrService descriptionAttrService;

    public Page<ProductSpuResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
//        return productSpuRepository.findProducts(pageable).map(p -> ProductSpuResponse.builder()
//                .id(p.getId())
//                .name(p.getName())
//                .price(p.getProductSkus().getFirst().getPrice())
//                .image(p.getImage())
//                .sold(p.getSold())
//                .originalPrice(p.getProductSkus().getFirst().getOriginalPrice())
//                .discountRate((int) (p.getProductSkus().getFirst().getDiscountRate() * 100))
//                .build());

        return productSpuRepository.findProducts(pageable).map(productSpuMapper::toResponse);
    }

    public Page<ProductSpuResponse> getProductsByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
//        return productSpuRepository.findByCategory(categoryId, pageable).map(p -> ProductSpuResponse.builder()
//                .id(p.getId())
//                .name(p.getName())
//                .originalPrice(p.getProductSkus().getFirst().getOriginalPrice())
//                .price(p.getProductSkus().getFirst().getPrice())
//                .discountRate((int) (p.getProductSkus().getFirst().getDiscountRate() * 100))
//                .sold(p.getSold())
//                .image(p.getImage())
//                .build());

        return productSpuRepository.findByCategory(categoryId,pageable).map(productSpuMapper::toResponse);

    }

    public ProductDetailResponse detailProduct(String id) {
        ProductSpu spu = productSpuRepository.findDetailById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        // lấy 1 lần để tránh gọi nhiều
        Set<ProductSku> skus = spu.getProductSkus();
        double minPrice = findMinPriceSpuId(skus);

        Set<ProductSkuResponse> setSku = skus.stream()
                .map(s -> ProductSkuResponse.builder()
                        .id(s.getId())
                        .product_spu_id(spu.getId())
                        .skuCode(s.getSkuCode())
                        .sort(s.getSort())
                        .originalPrice(s.getOriginalPrice())
                        .image(s.getImage())
                        .build())
                .sorted(Comparator.comparing(ProductSkuResponse::getSort))
                .collect(Collectors.toCollection(LinkedHashSet::new));


        Set<DescriptionAttrResponse> setAttrs =
                spu.getAttrs().stream().map(descriptionAttrMapper::toResponse).collect(Collectors.toSet());

        Set<ProductSkuAttrResponse> skuAttrs =
                spu.getSkuAttrs().stream().map(productSkuAttrMapper::toResponse).collect(Collectors.toSet());

        // Breadcrumb
        Category category = spu.getCategory();
        List<CategoryResponse> mapCate = categoryService.categoryProduct(category);
        List<BreadCrumbResponse> breadCrumbs = mapCate.stream()
                .map(categoryMapper::toBreadCrumbResponse)
                .collect(Collectors.toCollection(ArrayList::new));

        breadCrumbs.add(BreadCrumbResponse.builder()
                .url(spu.getKey())
                .categoryId("0")
                .name(spu.getName())
                .build());

        // Build response
        ProductDetailResponse detail = ProductDetailResponse.builder()
                .id(spu.getId())
                .name(spu.getName())
                .description(spu.getDescription())
                .shortDescription(spu.getShortDescription())
                .skus(setSku)
                .sold(spu.getSold())
                .originalPrice(minPrice)
//                .originalPrice(spu.getMinPrice())
                .desAttrs(setAttrs)
                .skuAttrs(skuAttrs)
                .breadCrumb(breadCrumbs)
                .brand(brandMapper.toResponse(spu.getBrand()))
                .productSameBrand(productsSameBrand(spu.getBrand().getId()))
                .productsSameCategory(productsSameCategory(category.getId()))
                .build();

        detail.setMedia(spu.getMedia());
        return detail;
    }


    private List<ProductSpuResponse> productsSameBrand(String brandId) {





        return productSpuRepository.findRandom20ByBrandId(brandId).stream().map(spu -> {
            ProductSpuResponse spuResponse = productSpuMapper.toResponse(spu);
            spuResponse.setOriginalPrice(findMinPriceSpuId(spu.getProductSkus()));
            return spuResponse;
        }).toList();
    }

    private List<ProductSpuResponse> productsSameCategory(String categoryId){

        return productSpuRepository.findRandom20ByCategoryId(categoryId)
                .stream().map(spu -> {
                    ProductSpuResponse spuResponse = productSpuMapper.toResponse(spu);
                    spuResponse.setOriginalPrice(findMinPriceSpuId(spu.getProductSkus()));
                    return spuResponse;
                }).toList();

    }


    private double findMinPriceSpuId(Set<ProductSku> setSkus){

        return setSkus.stream().map(ProductSku::getOriginalPrice).min(Double::compareTo).orElse(0.0);
    }

    public SpuCreateResponse createProductSpu(ProductSpuCreateRequest request, List<MultipartFile> files, String imageDir) throws IOException {
        ProductSpu spu = productSpuMapper.toProductSpu(request);

        String spu_id = UUID.randomUUID().toString();

        spu.setId(spu_id);

        spu.setKey(AppUtils.convertToSpuUrl(AppUtils.toSlug(spu.getName()),spu_id));

        List<DescriptionAttrRequest> attrs = request.getDescriptionAttrRequests();


        Category c = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));
        Brand b = brandRepository.findById(request.getBrandId()).orElseThrow(()->new AppException(ErrorCode.BRAND_NOT_EXISTED));

        spu.setCategory(c);
        spu.setBrand(b);

        String key = c.getKey();

        List<String> lstMedia = saveProductImages(key,spu_id,files,imageDir);

        String[] media =  lstMedia.toArray(new String[0]);
        spu.setMedia(AppUtils.arrayToPythonList(media));

        spu.setImage(media[0]);

        spu.setStockStatus(ProductSpu.StockStatus.InStock);

        spu.setDeleteStatus(ProductSpu.DeleteStatus.Active);

        spu.setSort(0);

        spu.setSold(0);

        SpuCreateResponse resp = productSpuMapper.toSpuCreateResponse(productSpuRepository.save(spu));

        resp.setMedia(media);

        resp.setDescriptionAttrs(descriptionAttrService.createDescriptionAttrs(request.getDescriptionAttrRequests(),spu));

        return resp;

    }


    public List<String> saveProductImages(String key, String spuId, List<MultipartFile> files, String imageDir) {
        List<String> mediaUrls = new ArrayList<>();
        Path uploadDirPath = Paths.get(imageDir, key);

        // Tạo thư mục upload nếu chưa tồn tại
        try {
            Files.createDirectories(uploadDirPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload: " + uploadDirPath, e);
        }

        int index = 0;
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "unknown.jpg";

            try {
                // Đọc ảnh từ MultipartFile
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    System.err.println("File upload không hợp lệ (không phải ảnh): " + originalName);
                    continue; // bỏ qua file này
                }

                // Lấy định dạng gốc nếu muốn giữ
                String ext = "jpg"; // mặc định chuyển về jpg
                String fileName = spuId + "_" + index++ + "." + ext;
                Path targetPath = uploadDirPath.resolve(fileName);

                // Resize 550x550 và lưu
                Thumbnails.of(image)
                        .size(550, 550)
                        .outputFormat(ext)
                        .toFile(targetPath.toFile());

                mediaUrls.add("/" + key + "/" + fileName);

                System.out.println("Lưu thành công file: " + fileName + " (" + file.getContentType() + ", " + file.getSize() + " bytes)");

            } catch (IOException e) {
                System.err.println("Lỗi khi xử lý file: " + originalName);
                e.printStackTrace();
            }
        }

        return mediaUrls;
    }




}
