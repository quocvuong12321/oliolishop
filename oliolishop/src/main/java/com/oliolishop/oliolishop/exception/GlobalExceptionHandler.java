package com.oliolishop.oliolishop.exception;


import com.oliolishop.oliolishop.dto.api.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handlingDataIntegrityViolation(DataIntegrityViolationException exception) {
        log.error("Data Integrity Violation: {}", exception.getMessage());

        // Mặc định là lỗi chung về vi phạm ràng buộc DB (4000)
        ErrorCode errorCode = ErrorCode.DATABASE_INTEGRITY_VIOLATION;
        String message = errorCode.getMessage();

        // Cố gắng lấy thông báo lỗi gốc để xác định loại lỗi cụ thể hơn
        if (exception.getCause() != null && exception.getCause().getCause() != null) {
            String rootMessage = exception.getCause().getCause().getMessage();

            // --- Phân tích Lỗi Trùng Lặp (UNIQUE) - Ưu tiên dùng mã 3xxx ---
            if (rootMessage.contains("Duplicate entry") || rootMessage.contains("duplicate key value violates unique constraint")) {
                // Trùng Phone
                if (rootMessage.contains("phonenumber") || rootMessage.contains("phone_number_key")) {
                    errorCode = ErrorCode.PHONE_EXISTED; // 3018
                }
                // Trùng Email
                else if (rootMessage.contains("email") || rootMessage.contains("email_key")) {
                    errorCode = ErrorCode.EMAIL_EXISTED; // 3010
                }
                // Trùng các trường khác (Ví dụ: tên Brand)
                else if (rootMessage.contains("brand_name_key")) {
                    errorCode = ErrorCode.BRAND_EXISTED; // 3005
                }
                // Trùng Account/Username (Fallback nếu không rõ trường)
                else {
                    errorCode = ErrorCode.ACCOUNT_EXISTED; // 3001 (Nếu là lỗi Account)
                }

                message = errorCode.getMessage(); // Cập nhật message theo lỗi 3xxx cụ thể

            }
            // --- Phân tích Lỗi Khóa Ngoại (FOREIGN KEY) - Dùng mã 4001 ---
            else if (rootMessage.contains("Foreign key constraint violation") || rootMessage.contains("foreign key constraint fails")) {
                errorCode = ErrorCode.FOREIGN_KEY_VIOLATION; // 4001
                message = errorCode.getMessage();
            }
            // --- Phân tích Lỗi Not Null - Dùng mã 4002 (hoặc 1006) ---
            else if (rootMessage.contains("violates not-null constraint") || rootMessage.contains("Cannot insert the value NULL")) {
                errorCode = ErrorCode.NOT_NULL_VIOLATION; // 4002
                message = errorCode.getMessage();
            }
            // --- Lỗi khác: Dùng 4000 (DATABASE_INTEGRITY_VIOLATION) đã set mặc định ---
        }

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .status(errorCode.getStatusCode().value())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }


}

