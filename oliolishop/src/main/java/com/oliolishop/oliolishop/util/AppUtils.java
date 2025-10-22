package com.oliolishop.oliolishop.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.CustomerAuthenticationService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AppUtils {
    public static String toSlug(String input) {
        if (input == null) return "";

        // Thay thế riêng chữ đ/Đ
        String replaced = input.replaceAll("đ", "d").replaceAll("Đ", "D");

        // Chuẩn hóa chuỗi Unicode (NFD tách ký tự + dấu)
        String normalize = Normalizer.normalize(replaced, Normalizer.Form.NFD);

        // Xóa dấu tiếng Việt (các ký tự dấu kết hợp)
        String noDiacritics = normalize.replaceAll("\\p{M}", "");

        // Chuyển thường
        String lower = noDiacritics.toLowerCase();

        // Thay ký tự không phải a-z,0-9 thành dấu gạch ngang
        String slug = lower.replaceAll("[^a-z0-9]+", "-");

        // Loại bỏ gạch ngang đầu/cuối
        return slug.replaceAll("(^-|-$)", "");
    }

    public static String convertToURL(String slug, String id) {
        return slug + "/c" + id;
    }

    public static String convertToSpuUrl(String slug, String spu_id) {
        return slug + "/p" + spu_id;
    }


    public static String[] parseStringToArray(String s) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            // Thay ' thành " để JSON parse được
            String json = s.replace("'", "\"");
            return mapper.readValue(json, String[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static String arrayToPythonList(String[] arr) {
        if (arr == null || arr.length == 0) return "[]";

        return "[" + Arrays.stream(arr)
                .map(s -> "'" + s.replace("'", "\\'") + "'") // escape dấu '
                .collect(Collectors.joining(", ")) + "]";
    }

    public static String generateId(long id) {
        String idStr = String.valueOf(id);
        int totalLength = 10;
        int zerosToAdd = totalLength - idStr.length();

        StringBuilder genId = new StringBuilder();
        for (int i = 0; i < zerosToAdd; i++) {
            genId.append("0");
        }
        genId.append(idStr);

        return genId.toString();
    }

//    public static String saveImage(MultipartFile file,
//                            String imageDir,
//                            String folder,
//                            String fileName) throws IOException {
//        Path uploadDirPath = Paths.get(imageDir, folder);
//        Files.createDirectories(uploadDirPath);
//
//        BufferedImage image = ImageIO.read(file.getInputStream());
//        if (image == null) {
//            throw new IOException("File upload không hợp lệ (không phải ảnh): " + file.getOriginalFilename());
//        }
//
//        // Luôn convert sang jpg
//        String ext = "jpg";
//        if (!fileName.endsWith("." + ext)) {
//            fileName = fileName + "." + ext;
//        }
//        Path targetPath = uploadDirPath.resolve(fileName);
//
//        // Resize 500x500 và lưu
//        Thumbnails.of(image)
//                .size(500, 500)
//                .outputFormat(ext)
//                .toFile(targetPath.toFile());
//
//        return folder + "/" + fileName;
//    }

    public static String saveImage(MultipartFile file,
                                   String imageDir,
                                   String folder,
                                   String fileNameBase) throws IOException { // Đổi tên fileName thành fileNameBase cho rõ ràng

        // 1. Tạo thư mục upload nếu chưa tồn tại
        Path uploadDirPath = Paths.get(imageDir, folder);
        Files.createDirectories(uploadDirPath);

        // 2. Xác định định dạng (Extension) và Tên file cuối cùng
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Ưu tiên PNG nếu nội dung là PNG (để giữ nền trong suốt), nếu không thì dùng JPG
        String outputExt = "jpg";
        if (contentType != null && contentType.toLowerCase().contains("png")) {
            outputExt = "png";
        }

        // Tên file cuối cùng (baseName + .ext)
        String finalFileName = fileNameBase + "." + outputExt;
        Path targetPath = uploadDirPath.resolve(finalFileName);

        // 3. Xử lý ảnh bằng Thumbnailator (Tối ưu đọc/ghi)
        try {
            Thumbnails.of(file.getInputStream())
                    // Giữ tỷ lệ ảnh (Aspect Ratio), giới hạn tối đa 800x800.
                    // Nếu ảnh nhỏ hơn, nó sẽ không phóng to.
                    .size(500, 500)
                    .keepAspectRatio(true) // Rất quan trọng: GIỮ TỶ LỆ GỐC

                    // Chất lượng (Chỉ áp dụng cho JPG)
                    .outputQuality(0.9)

                    // Định dạng đầu ra
                    .outputFormat(outputExt)
                    .toFile(targetPath.toFile());
        } catch (IOException e) {
            throw new IOException("Lỗi khi xử lý và lưu file ảnh: " + file.getOriginalFilename(), e);
        }

        // 4. Trả về URL tương đối
        return folder + "/" + finalFileName;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String getCustomerIdByJwt() {
        Authentication authentication = CustomerAuthenticationService.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED); // Ném lỗi nếu chưa đăng nhập
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String customerId = jwt.getClaim("customerId");
            if (customerId == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            return customerId;
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    public static String getOptionalCustomerId() {
        Authentication authentication = CustomerAuthenticationService.getAuthentication();

        // Nếu không được xác thực hoặc là anonymous, trả về null
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        // Lấy Customer ID từ JWT
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("customerId"); // Có thể là null nếu claim không có, nhưng đã qua kiểm tra is-authenticated
        }

        return null; // Trường hợp không xác định
    }

    public static String getEmployeeIdByJwt() {
        Authentication authentication = CustomerAuthenticationService.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED); // Ném lỗi nếu chưa đăng nhập
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String employeeId = jwt.getClaim("employeeId");
            if(employeeId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);
            return employeeId;
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    public static LocalDateTime pasteStringToDateTime(String dateTime){
        ZoneId localZone = ZoneId.of("Asia/Ho_Chi_Minh");
        Instant instant = Instant.parse(dateTime);
        return LocalDateTime.ofInstant(instant,localZone);
    }
}
