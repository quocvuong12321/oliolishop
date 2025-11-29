package com.oliolishop.oliolishop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // 1xxx: bad request (Lỗi dữ liệu đầu vào)
    INVALID_KEY(1001,"Khóa tin nhắn không hợp lệ",HttpStatus.BAD_REQUEST),
    EMPTY_USERNAME(1002,"Tên người dùng không được để trống",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Tên người dùng phải có ít nhất {min} ký tự",HttpStatus.BAD_REQUEST),
    DOB_INVALID(1004,"Bạn phải đủ {min} tuổi trở lên",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Mật khẩu phải có ít nhất 8 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt", HttpStatus.BAD_REQUEST),
    VALUE_REQUIRED(1006, "Giá trị không được để trống", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(1007,"Tên không được để trống",HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1008, "Mật khẩu và mật khẩu nhập lại không khớp", HttpStatus.BAD_REQUEST),
    QUANTITY_POSITIVE(1009,"Số lượng phải lớn hơn 0",HttpStatus.BAD_REQUEST),
    PRICE_POSITIVE(1010,"Giá phải lớn hơn 0",HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_INVALID_FORMAT(1011,"Số điện thoại không đúng định dạng",HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_QUANTITY_PRODUCT(1012,"Sản phẩm không đủ số lượng",HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_QUANTITY_VOUCHER(1013,"Voucher không đủ số lượng",HttpStatus.BAD_REQUEST),
    VOUCHER_HAS_EXPIRED(1014,"Voucher đã hết hạn",HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_FORMAT(1015,"Email không đúng định dạng",HttpStatus.BAD_REQUEST),
    START_DATE_MUST_BEFORE(1016,"Ngày bắt đầu phải trước hoặc bằng ngày kết thúc",HttpStatus.BAD_REQUEST),
    QUARTER_INVALID(1017,"Quý phải là 1-4",HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_USAGE_VOUCHER(1018,"Bạn đã hết lượt sử dụng voucher",HttpStatus.NOT_ACCEPTABLE),
    DUPLICATE_PERMISSION(1019,"Permission Id đã tồn tại",HttpStatus.CONFLICT),
    INVALID_IMAGE_FORMAT(1020,"Định dạng ảnh không hợp lệ",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_VALIDATION(1999,"Lỗi valid không xác định",HttpStatus.BAD_REQUEST),

    // 2xxx: auth (Lỗi xác thực và ủy quyền)
    UNAUTHENTICATED(2001,"Chưa được xác thực/Đăng nhập",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002,"Bạn không có quyền truy cập",HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_EXPIRED(2003,"Refresh token đã hết hạn",HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(2004,"Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(2005, "Mã OTP không hợp lệ",HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(2006,"Mã OTP đã hết hạn",HttpStatus.UNAUTHORIZED),
    ACCOUNT_HAS_BLOCKED(2007,"Tài khoản đã bị khóa",HttpStatus.LOCKED),

    // 3xxx: business logic (Lỗi nghiệp vụ)
    ACCOUNT_EXISTED(3001,"Tài khoản đã tồn tại",HttpStatus.CONFLICT),
    ACCOUNT_NOT_EXISTED(3002, "Tài khoản không tồn tại",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(3003, "Mật khẩu không đúng",HttpStatus.UNAUTHORIZED),
    BRAND_NOT_EXISTED(3004, "Thương hiệu không tồn tại",HttpStatus.NOT_FOUND),
    BRAND_EXISTED(3005,"Thương hiệu đã tồn tại",HttpStatus.CONFLICT),
    CATEGORY_NOT_EXIST(3006,"Danh mục không tồn tại",HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXIST(3007,"Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    ATTRIBUTE_EXISTED(3008, "Thuộc tính mô tả đã tồn tại", HttpStatus.CONFLICT),
    ATTRIBUTE_NOT_EXIST(3009, "Thuộc tính mô tả không tồn tại",HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(3010,"Email đã tồn tại",HttpStatus.CONFLICT),
    ROLE_NOT_EXIST(3011,"Vai trò (Role) không tồn tại",HttpStatus.NOT_FOUND),
    EMPTY_PRODUCT_SKU(3012,"Danh sách Product SKU không được để trống",HttpStatus.BAD_REQUEST),
    CUSTOMER_NOT_EXISTED(3013,"Khách hàng không tồn tại",HttpStatus.NOT_FOUND),
    DISCOUNT_RULE_NOT_EXISTED(3014,"Quy tắc giảm giá không tồn tại",HttpStatus.NOT_FOUND),
    CART_EXISTED(3015,"Giỏ hàng đã tồn tại",HttpStatus.CONFLICT),
    CART_NOT_EXISTED(3016,"Giỏ hàng của bạn còn trống",HttpStatus.NOT_FOUND),
    INVALID_REQUEST(3017, "Yêu cầu không hợp lệ",HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(3018,"Số điện thoại đã tồn tại",HttpStatus.CONFLICT),
    ADDRESS_NOT_EXIST(3019, "Địa chỉ không tồn tại",HttpStatus.NOT_FOUND),
    VOUCHER_NOT_EXISTED(3020,"Voucher không hợp lệ hoặc không tồn tại", HttpStatus.NOT_FOUND),
    NOT_ENOUGH_STOCK(3021,"Số lượng sản phẩm trong kho không đủ",HttpStatus.CONFLICT),
    ORDER_NOT_EXISTED(3022, "Đơn hàng không tồn tại",HttpStatus.NOT_FOUND),
    PAYMENT_INVALID(3023,"Chữ ký/Thông tin thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    TRANSACTION_NOT_EXIST(3024,"Giao dịch thanh toán không hợp lệ hoặc không tồn tại",HttpStatus.NOT_FOUND),
    ORDER_PAID(3025,"Đơn hàng đã được thanh toán",HttpStatus.FORBIDDEN),
    ORDER_STATUS_INVALID(3026,"Trạng thái đơn hàng không hợp lệ",HttpStatus.BAD_REQUEST),
    RATED(3027,"Bạn đã đánh giá đơn hàng này rồi",HttpStatus.CONFLICT),
    CREATE_SHIPPING_FAIL(3028,"Tạo đơn vận chuyển thất bại", HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_EXIST(3029, "Nhân viên không tồn tại", HttpStatus.BAD_REQUEST),
    BANNER_NOT_EXIST(3030,"Banner không tồn tại",HttpStatus.BAD_REQUEST),
    POLICY_NOT_EXIST(3031,"Chính sách không tồn tại", HttpStatus.NOT_FOUND),
    PDF_NOT_FOUND(3032,"Không tìm thấy file pdf",HttpStatus.NOT_FOUND),
    POLICY_EXISTED(3033, "Chính sách đã tồn tại",HttpStatus.CONFLICT),

    // Lỗi chung khi vi phạm ràng buộc DB (ví dụ: để fallback)
    DATABASE_INTEGRITY_VIOLATION(4000, "Vi phạm ràng buộc dữ liệu cơ sở. Vui lòng kiểm tra lại.", HttpStatus.CONFLICT),
    FOREIGN_KEY_VIOLATION(4001, "Không thể thực hiện do có dữ liệu liên quan đang sử dụng.", HttpStatus.CONFLICT),
    NOT_NULL_VIOLATION(4002, "Thiếu giá trị bắt buộc cho trường dữ liệu.", HttpStatus.BAD_REQUEST),
    DATA_TOO_LONG(4003, "Dữ liệu nhập vào quá dài so với quy định.", HttpStatus.BAD_REQUEST),
    CHECK_CONSTRAINT_VIOLATION(4004, "Dữ liệu không thỏa mãn điều kiện ràng buộc.", HttpStatus.BAD_REQUEST),
    // 9xxx: system (Lỗi hệ thống)
    UNCATEGORIZED_EXCEPTION(9999,"Lỗi không xác định/Lỗi hệ thống vui lòng báo Dev", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private int Code;
    private String message;
    private HttpStatusCode statusCode;



}
