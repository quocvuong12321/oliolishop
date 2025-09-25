package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.image.ProductSpuImageUpdateRequest;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateResponse;
import com.oliolishop.oliolishop.service.ProductSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE + ApiPath.IMAGE)
public class ImageController {

    @Autowired
    ProductSpuService productSpuService;
    @Value("${app.image-dir}")
    private String imageDir; // D:/HocTap/AI/crawl/images

    @GetMapping("/{folder}/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String folder,
            @PathVariable String filename) {
        try {
            // Build đường dẫn tuyệt đối: baseDir/folder/filename
            Path filePath = Paths.get(imageDir)
                    .resolve(folder)
                    .resolve(filename)
                    .normalize();

            System.out.println("Full path: " + filePath.toAbsolutePath());

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = java.nio.file.Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = ApiPath.BY_ID, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Boolean> updateImageProductSpu(@PathVariable("id") String id,
                                                      @RequestPart("existingImages")ProductSpuImageUpdateRequest request,
                                                      @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles) throws IOException{

        ProductSpuCreateResponse resp =
                productSpuService.updateProductSpuImages(id,request.getExistingImages(),newFiles,imageDir,request.getThumbnailIndex());

        return ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }



}
