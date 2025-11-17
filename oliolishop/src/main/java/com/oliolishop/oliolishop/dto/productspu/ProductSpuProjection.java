package com.oliolishop.oliolishop.dto.productspu;

public interface ProductSpuProjection {
    String getProductSpuId();
    String getName();
    String getCategoryId();
    String getBrandId();
    Double getMinPrice();
    Double getMaxPrice();
    String getImage();
    String getDeleteStatus();
    Integer getTotalQuantitySold();
}
