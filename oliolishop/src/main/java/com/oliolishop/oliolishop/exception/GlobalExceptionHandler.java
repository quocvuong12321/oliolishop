package com.oliolishop.oliolishop.exception;


import com.oliolishop.oliolishop.dto.api.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Slf4j
@RestControllerAdvice // Thêm khoảng trắng ở đây
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
        apiResponse.setStatus(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode().value());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Xử lý các lỗi do mình định nghĩa và chủ động throw ra (AppException)
     */
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception){
        ErrorCode errorCode=exception.getErrorCode();
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .status(errorCode.getStatusCode().value())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    /**
     * Xử lý các lỗi validation khi sử dụng @Valid (dành cho object trong request body)
     */
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception){
//        String enumKey = null;
//
//        // Lấy defaultMessage từ field lỗi (được dùng như key để tra ErrorCode)
//        if (exception.getFieldError() != null) {
//            enumKey = exception.getFieldError().getDefaultMessage();
//        }
//        ErrorCode errorCode=ErrorCode.INVALID_KEY;
//        Map<String,Object> attributes = null;
//
//        try {
//            // Nếu tìm được key hợp lệ thì lấy ErrorCode tương ứng
//            if (enumKey != null) {
//                errorCode = ErrorCode.valueOf(enumKey);
//                // Lấy thông tin constraint (ví dụ min=18) để format message
//                var constraintViolation = exception.getBindingResult().getAllErrors()
//                        .getFirst().unwrap(ConstraintViolation.class);
//                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
//
//                log.info(attributes.toString());
//            }
//        } catch (IllegalArgumentException e) {
//            // Không tìm thấy enum, giữ nguyên INVALID_KEY
//        }
//        ApiResponse<Object> apiResponse = new ApiResponse<>();
//
//        // Nếu có thông tin thuộc tính (min/max) thì thay vào message
//        apiResponse.setCode(errorCode.getCode());
//        apiResponse.setMessage(Objects.nonNull(attributes)
//                ?mapAttributes(errorCode.getMessage(),attributes)
//                :errorCode.getMessage());
//        return  ResponseEntity.badRequest().body(apiResponse);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        try {
            log.info("Handling validation exception: {}", exception.getMessage());

            List<Map<String, Object>> errors = new ArrayList<>();

            for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
                String enumKey = fieldError.getDefaultMessage();
                ErrorCode errorCode = ErrorCode.INVALID_KEY;
                Map<String, Object> attributes = null;

                try {
                    if (enumKey != null) {
                        errorCode = ErrorCode.valueOf(enumKey);

                        var constraintViolation = fieldError.unwrap(ConstraintViolation.class);
                        attributes = constraintViolation.getConstraintDescriptor().getAttributes();
                    }
                } catch (Exception e) {
                    // giữ nguyên INVALID_KEY nếu không map được
                }

                Map<String, Object> errorDetail = new HashMap<>();
                errorDetail.put("field", fieldError.getField());
                errorDetail.put("code", errorCode.getCode());
                errorDetail.put("message",
                        attributes != null
                                ? mapAttributes(errorCode.getMessage(), attributes)
                                : errorCode.getMessage());

                errors.add(errorDetail);
            }

            ApiResponse<Object> apiResponse = new ApiResponse<>();
            apiResponse.setCode(ErrorCode.INVALID_KEY.getCode());
            apiResponse.setMessage("Validation failed");
            apiResponse.setResult(errors);
            apiResponse.setStatus(400); // Đặt status code rõ ràng

            return ResponseEntity.status(400).body(apiResponse); // Đặt status code tường minh
        } catch (Exception e) {
            log.error("Error in exception handler", e);
            ApiResponse<Object> fallbackResponse = new ApiResponse<>();
            fallbackResponse.setCode(9999);
            fallbackResponse.setMessage("Unexpected error in validation: " + e.getMessage());
            fallbackResponse.setStatus(400); // Đặt status code rõ ràng
            return ResponseEntity.status(400).body(fallbackResponse);
        }
    }


    /**
     * Xử lý các lỗi validation của @Validated (thường dùng với @RequestParam, @PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        // Lấy thông báo lỗi đầu tiên
        String enumKey = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("VALIDATION_ERROR");

        // Thử chuyển đổi message thành ErrorCode
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // Giữ nguyên INVALID_KEY nếu không map được
        }

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    /**
     * Hàm thay thế biến trong message (ví dụ: {min}) bằng giá trị thực tế từ annotation
     */
    private String mapAttributes(String message, Map<String, Object> attributes) {
        if (attributes == null || !attributes.containsKey(MIN_ATTRIBUTES)) {
            return message; // Trả về message gốc nếu không có thuộc tính min
        }

        try {
            String minValue = attributes.get(MIN_ATTRIBUTES).toString();
            return message.replace("{" + MIN_ATTRIBUTES + "}", minValue);
        } catch (Exception e) {
            log.error("Error mapping attribute: {}", e.getMessage());
            return message;
        }
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

