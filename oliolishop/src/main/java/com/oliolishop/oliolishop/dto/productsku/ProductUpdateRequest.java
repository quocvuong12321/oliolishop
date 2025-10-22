package com.oliolishop.oliolishop.dto.productsku;


import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ProductUpdateRequest {
    @Positive(message ="QUANTITY_POSITIVE")
    BigDecimal originalPrice;
    @Positive(message ="QUANTITY_POSITIVE")
    double weight;
    @Positive(message ="QUANTITY_POSITIVE")
    int skuStock;
}
