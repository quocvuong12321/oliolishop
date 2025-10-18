package com.oliolishop.oliolishop.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingRequest {
    String orderItemId;
    String productSpuId;
    @Min(1) @Max(5)
    double star;
    String comment;
    List<MultipartFile> attachedFiles;
}
