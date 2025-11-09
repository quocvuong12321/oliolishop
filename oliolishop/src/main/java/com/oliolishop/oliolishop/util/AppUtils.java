package com.oliolishop.oliolishop.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.CustomerAuthenticationService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Set;
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
//                                   String imageDir,
//                                   String folder,
//                                   String fileNameBase) throws IOException { // Đổi tên fileName thành fileNameBase cho rõ ràng
//
//        // 1. Tạo thư mục upload nếu chưa tồn tại
//        Path uploadDirPath = Paths.get(imageDir, folder);
//        Files.createDirectories(uploadDirPath);
//
//        // 2. Xác định định dạng (Extension) và Tên file cuối cùng
//        String originalFilename = file.getOriginalFilename();
//        String contentType = file.getContentType();
//
//        // Ưu tiên PNG nếu nội dung là PNG (để giữ nền trong suốt), nếu không thì dùng JPG
//        String outputExt = "jpg";
//        if (contentType != null && contentType.toLowerCase().contains("png")) {
//            outputExt = "png";
//        }
//
//        // Tên file cuối cùng (baseName + .ext)
//        String finalFileName = fileNameBase + "." + outputExt;
//        Path targetPath = uploadDirPath.resolve(finalFileName);
//
//        // 3. Xử lý ảnh bằng Thumbnailator (Tối ưu đọc/ghi)
//        try {
//            Thumbnails.of(file.getInputStream())
//                    // Giữ tỷ lệ ảnh (Aspect Ratio), giới hạn tối đa 800x800.
//                    // Nếu ảnh nhỏ hơn, nó sẽ không phóng to.
//                    .size(500, 500)
//                    .keepAspectRatio(true) // Rất quan trọng: GIỮ TỶ LỆ GỐC
//
//                    // Chất lượng (Chỉ áp dụng cho JPG)
//                    .outputQuality(0.9)
//
//                    // Định dạng đầu ra
//                    .outputFormat(outputExt)
//                    .toFile(targetPath.toFile());
//        } catch (IOException e) {
//            throw new IOException("Lỗi khi xử lý và lưu file ảnh: " + file.getOriginalFilename(), e);
//        }
//
//        // 4. Trả về URL tương đối
//        return folder + "/" + finalFileName;
//    }

    public static String saveImage(MultipartFile file,
                                   String imageDir,
                                   String folder,
                                   String fileNameBase) throws IOException {

        // 1. Tạo thư mục upload nếu chưa tồn tại
        Path uploadDirPath = Paths.get(imageDir, folder);
        Files.createDirectories(uploadDirPath);

        // 2. Xác định định dạng đầu vào và đầu ra
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        // Lấy phần mở rộng từ tên file nếu có
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        // Một số định dạng ảnh hợp lệ
        Set<String> supportedFormats = Set.of("jpg", "jpeg", "png", "bmp", "gif");

        // Nếu đuôi không nằm trong danh sách, fallback về jpg
        if (!supportedFormats.contains(extension)) {
            extension = "jpg";
        }

        String finalFileName = fileNameBase + "." + extension;
        Path targetPath = uploadDirPath.resolve(finalFileName);

        try {
            Thumbnails.of(file.getInputStream())
                    .size(800, 800)
                    .keepAspectRatio(true)
                    .outputQuality(0.9)
                    .outputFormat(extension)
                    .toFile(targetPath.toFile());
        } catch (IOException e) {
            throw new IOException("Lỗi khi xử lý ảnh: " + originalFilename, e);
        }

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

    public static RestTemplate createUnsafeRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            return new RestTemplate(factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
