package com.oliolishop.oliolishop.dto.productspu;

import lombok.Data;
import java.util.List;

@Data
public class ImageSearchResponseDTO {
    private String status;
    private String message;
    private int total;
    private List<ImageSearchResultDTO> results;
}
