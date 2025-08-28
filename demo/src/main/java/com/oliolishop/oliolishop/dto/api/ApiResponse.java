package com.oliolishop.oliolishop.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;
    @Builder.Default
    String message = "Successfully";
    T result;

//    public static <T> ApiResponse<T> success(T result) {
//        return ApiResponse.<T>builder()
//                .result(result)
//                .code(1000)
//                .message("Successfully")
//                .build();
//    }

//    public static <T> ApiResponse<T> error(int code, String message) {
////        ApiResponse<T> res = new ApiResponse<>();
////        res.code = code;
////        res.message = message;
////        return res;
//    }
}
