package com.oliolishop.oliolishop.exception;


import com.oliolishop.oliolishop.dto.api.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice // Đánh dấu class này là global exception handler cho toàn bộ controller
public class GlobalExceptionHandler { //Class chịu trách nhiệm handling exception
    // Key dùng để lấy giá trị ràng buộc "min" từ annotation @Min
    private static final String MIN_ATTRIBUTES = "min";

    /**
     * Xử lý các lỗi Runtime chưa được phân loại
     */
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(RuntimeException exception){
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        log.info("Unhandled Exception: {}",exception.getMessage());

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Xử lý các lỗi do mình định nghĩa và chủ động throw ra (AppException)
     */
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception){
        ErrorCode errorCode=exception.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    /**
     * Xử lý các lỗi validation khi sử dụng @Valid (dành cho object trong request body)
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception){
        String enumKey = null;

        // Lấy defaultMessage từ field lỗi (được dùng như key để tra ErrorCode)
        if (exception.getFieldError() != null) {
            enumKey = exception.getFieldError().getDefaultMessage();
        }
        ErrorCode errorCode=ErrorCode.INVALID_KEY;
        Map<String,Object> attributes = null;

        try {
            // Nếu tìm được key hợp lệ thì lấy ErrorCode tương ứng
            if (enumKey != null) {
                errorCode = ErrorCode.valueOf(enumKey);
                // Lấy thông tin constraint (ví dụ min=18) để format message
                var constraintViolation = exception.getBindingResult().getAllErrors()
                        .getFirst().unwrap(ConstraintViolation.class);
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();

                log.info(attributes.toString());
            }
        } catch (IllegalArgumentException e) {
            // Không tìm thấy enum, giữ nguyên INVALID_KEY
        }
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        // Nếu có thông tin thuộc tính (min/max) thì thay vào message
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes)
                ?mapAttributes(errorCode.getMessage(),attributes)
                :errorCode.getMessage());
        return  ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Xử lý các lỗi validation của @Validated (thường dùng với @RequestParam, @PathVariable)
     */
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
//        // Lấy thông báo lỗi đầu tiên trong danh sách các vi phạm
//        String message = ex.getConstraintViolations().stream()
//                .map(ConstraintViolation::getMessage)
//                .findFirst()
//                .orElse("VALIDATION_ERROR");
//
//        return ResponseEntity.badRequest().body(ApiResponse.builder()
//                .code(ErrorCode.INVALID_KEY.getCode())
//                .message(message)
//                .build());
//    }

    /**
     * Hàm thay thế biến trong message (ví dụ: {min}) bằng giá trị thực tế từ annotation
     */
    private String mapAttributes(String message, Map<String,Object> attributes){
        String minValue = attributes.get(MIN_ATTRIBUTES).toString();

        return message.replace("{"+MIN_ATTRIBUTES+"}", minValue);
    }

    /**
     * Xử lý các lỗi khi người dùng không có quyền truy cập
     */
//    @ExceptionHandler(value = AccessDeniedException.class)
//    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception){
//        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
//
//        return ResponseEntity.status(errorCode.getStatusCode())
//                .body(ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build());
//    }


}

