package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.util.AppUtils;
import com.oliolishop.oliolishop.util.MultipartInputStreamFileResource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VectorApiService {

    RestTemplate restTemplate;

    String fastApiUrl;

    public VectorApiService(@Value("${app.fast-api.base-url}") String fastApiUrl ) {
        this.restTemplate = AppUtils.createUnsafeRestTemplate();
        this.fastApiUrl = fastApiUrl;
    }

    @Async
    public void addProductVectorsAsync(List<String> imageUrls, List<MultipartFile> files) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();


            for (String imageUrl : imageUrls) {
                body.add("imageUrls", imageUrl);
            }

            // Thêm file
            for (MultipartFile file : files) {
                body.add("files", new MultipartInputStreamFileResource(file));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String request = fastApiUrl + "/vector/add";

            ResponseEntity<String> response = restTemplate.exchange(
                    request,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vectors added successfully.");
            } else {
                System.out.println("⚠ Failed to add vectors, status=" + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error adding vectors");
            e.printStackTrace();
        }
    }

    @Async
    public void updateProductVectorsAsync(
            String productSpuId,
            List<String> deletedImages,       // list các ảnh bị xóa
            List<String> newImageUrls,        // list URL ảnh mới
            List<MultipartFile> newFiles      // list file ảnh mới
    ) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("productSpuId", productSpuId);

            // Thêm các ảnh bị xóa
            for (String url : deletedImages) {
                body.add("deletedImages", url);
            }

            // Thêm URL ảnh mới
            for (String url : newImageUrls) {
                body.add("newImageUrls", url);
            }

            // Thêm file mới
            for (MultipartFile file : newFiles) {
                body.add("newFiles", new MultipartInputStreamFileResource(file));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String request = fastApiUrl + "/vector/update";

            ResponseEntity<String> response = restTemplate.exchange(
                    request,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Product " + productSpuId + " vectors updated successfully.");
            } else {
                System.out.println("Failed to update vectors for product " + productSpuId +
                        ", status=" + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error updating vectors for product " + productSpuId);
            e.printStackTrace();
        }
    }
}
