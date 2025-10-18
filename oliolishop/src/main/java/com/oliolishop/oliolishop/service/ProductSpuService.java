package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.BreadCrumbResponse;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrRequest;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrResponse;
import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
import com.oliolishop.oliolishop.dto.productspu.*;
import com.oliolishop.oliolishop.entity.Brand;
import com.oliolishop.oliolishop.entity.Category;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSpu;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.*;
import com.oliolishop.oliolishop.repository.*;
import com.oliolishop.oliolishop.util.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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
    private final ProductSkuRepository productSkuRepository;
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
    RedisService redisService;


    public List<ProductSpuResponse> getProducts(String categoryId,  String brandId ,double minPrice, double maxPrice, int page, int size) {
        List<ProductSpuProjection> query = productSpuRepository.findProducts(categoryId,brandId,minPrice,maxPrice, page, size);
        List<ProductSpuResponse> lstResponse = new ArrayList<>();

        query.forEach(s->lstResponse.add(ProductSpuResponse.builder()
                        .id(s.getProductSpuId())
                        .brandId(s.getBrandId())
                        .categoryId(s.getCategoryId())
                        .maxPrice(s.getMaxPrice())
                        .minPrice(s.getMinPrice())
                        .image(s.getImage())
                        .name(s.getName())
                .build()));

        return  lstResponse;
    }


    public Integer getTotalElements(String categoryId, String brandId, double minPrice, double maxPrice){
        return productSpuRepository.getTotalElements(categoryId,brandId,minPrice,maxPrice);
    }

    public ProductDetailResponse detailProduct(String id) {
        ProductSpu spu = productSpuRepository.findDetailById(id, ProductSku.Status.Active)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        // lấy 1 lần để tránh gọi nhiều
        Set<ProductSku> skus = spu.getProductSkus();
        BigDecimal minPrice = findMinPriceSpuId(skus);

        Set<ProductSkuResponse> setSku = skus.stream()
                .map(s -> ProductSkuResponse.builder()
                        .id(s.getId())
                        .productSpuId(spu.getId())
                        .skuCode(s.getSkuCode())
                        .sort(s.getSort())
                        .originalPrice(s.getOriginalPrice())
                        .image(s.getImage())
                        .skuStock(s.getSkuStock())
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
                .brand(brandMapper.toResponse(spu.getBrand()))
                .productSameBrand(productsSameBrand(spu.getBrand().getId()))
                .productsSameCategory(productsSameCategory(category.getId()))
                .breadCrumb(breadCrumbs)
                .build();

        detail.setMedia(spu.getMedia());
        return detail;
    }


    protected List<ProductSpuResponse> productsSameBrand(String brandId) {
        List<ProductSpuResponse> lstResponse = new ArrayList<>();

        productSpuRepository.findProducts(null,brandId,0,9999999,0,20).forEach(
                s->lstResponse.add(ProductSpuResponse.builder()
                        .id(s.getProductSpuId())
                        .brandId(s.getBrandId())
                        .categoryId(s.getCategoryId())
                        .maxPrice(s.getMaxPrice())
                        .minPrice(s.getMinPrice())
                        .image(s.getImage())
                        .name(s.getName())
                        .build()));
        return  lstResponse;

    }

    protected List<ProductSpuResponse> productsSameCategory(String categoryId){

        List<ProductSpuResponse> lstResponse = new ArrayList<>();

        productSpuRepository.findProducts(categoryId,null,0,9999999,0,20).forEach(
                s->lstResponse.add(ProductSpuResponse.builder()
                        .id(s.getProductSpuId())
                        .brandId(s.getBrandId())
                        .categoryId(s.getCategoryId())
                        .maxPrice(s.getMaxPrice())
                        .minPrice(s.getMinPrice())
                        .image(s.getImage())
                        .name(s.getName())
                        .build()));
        return  lstResponse;

    }


    private BigDecimal findMinPriceSpuId(Set<ProductSku> setSkus){

        return setSkus.stream().map(ProductSku::getOriginalPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public ProductSpuCreateResponse createProductSpu(ProductSpuCreateRequest request, List<MultipartFile> files, String imageDir) throws IOException {
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

        ProductSpuCreateResponse resp = productSpuMapper.toSpuCreateResponse(productSpuRepository.save(spu));

        resp.setMedia(media);

        resp.setDescriptionAttrs(descriptionAttrService.createDescriptionAttrs(request.getDescriptionAttrRequests(),spu));

        return resp;

    }



    public List<String> saveProductImages(String key, String spuId, List<MultipartFile> files, String imageDir) {
        List<String> mediaUrls = new ArrayList<>();

        // 1. Tạo thư mục upload nếu chưa tồn tại (Đã có logic trong AppUtils.saveImage, nhưng giữ ở đây cũng được)
        // Tuy nhiên, việc tạo thư mục nên nằm trong hàm saveImage để cô đọng logic file I/O

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue; // Bỏ qua file rỗng

            try {
                // Tên file cơ sở: SPU_ID + index (không có đuôi)
                String fileNameBase = spuId + "_" + System.currentTimeMillis();

                // GỌI HÀM LƯU ẢNH CHUẨN
                // AppUtils.saveImage sẽ tự lo về: Tạo thư mục, Cắt/Resize (nếu cần), Đuôi file (.jpg/.png), và trả về URL tương đối.
                String url = AppUtils.saveImage(file, imageDir, key, fileNameBase);
                mediaUrls.add(url);

                log.info("Lưu thành công Product Image: {}", url);
            } catch (IOException e) {
                // Log lỗi và TIẾP TỤC xử lý các file khác
                log.error("Lỗi khi xử lý file ảnh sản phẩm {}: {}", file.getOriginalFilename(), e.getMessage());
                // KHÔNG THROW RuntimeException ở đây trừ khi bạn muốn hủy toàn bộ request
            }
        }
        return mediaUrls;
    }
    @Transactional
    public ProductSpuCreateResponse updateProductSpu(ProductSpuCreateRequest request, String id){
        ProductSpu spu = productSpuRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        spu.setName(request.getName());
        spu.setKey(AppUtils.convertToSpuUrl(AppUtils.toSlug(request.getName()),id));
        spu.setDescription(request.getDescription());
        spu.setShortDescription(request.getShortDescription());
        spu.setBrand(Brand.builder().id(request.getBrandId()).build());
        spu.setCategory(Category.builder().id(request.getCategoryId()).build());

        return productSpuMapper.toSpuCreateResponse(productSpuRepository.save(spu));
    }

    @Transactional
    public ProductSpuCreateResponse updateProductSpuImages(
            String spuId,
            List<String> existingImages,     // ảnh cũ user giữ lại
            List<MultipartFile> newFiles,    // ảnh mới user upload
            String imageDir,
            int thumbnailIndex) throws IOException {

        ProductSpu spu = productSpuRepository.findById(spuId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        String key = spu.getCategory().getKey();

        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }

        // Ảnh hiện tại trong DB (mutable list để remove được)
        List<String> oldImages = new ArrayList<>(Arrays.asList(AppUtils.parseStringToArray(spu.getMedia())));

        // Tìm ảnh bị xoá (có trong DB nhưng không nằm trong existingImages)
        List<String> finalExistingImages = existingImages;
        List<String> deleted = oldImages.stream()
                .filter(img -> !finalExistingImages.contains(img))
                .toList();

        // Xoá file vật lý trên disk + remove khỏi list
        for (String url : deleted) {
            Path path = Paths.get(imageDir, url);
            Files.deleteIfExists(path);
        }

        // Lưu ảnh mới upload
        List<String> newUrls = saveProductImages(key, spuId, newFiles, imageDir);

        // Ghép lại final list: ảnh còn giữ + ảnh mới
        List<String> finalImages = new ArrayList<>(existingImages);
        finalImages.addAll(newUrls);

        // Cập nhật DB
        String[] updatedMedia = finalImages.toArray(new String[0]);
        spu.setMedia(AppUtils.arrayToPythonList(updatedMedia));
        if (thumbnailIndex >= 0 && thumbnailIndex < finalImages.size()) {
            spu.setImage(finalImages.get(thumbnailIndex));
        } else if (!finalImages.isEmpty()) {
            spu.setImage(finalImages.getFirst());
        }

        ProductSpu saved = productSpuRepository.save(spu);

        // Trả về response
        ProductSpuCreateResponse resp = productSpuMapper.toSpuCreateResponse(saved);
        resp.setMedia(updatedMedia);
        return resp;
    }

}
