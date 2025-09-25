package com.oliolishop.oliolishop.ultils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.coobird.thumbnailator.Thumbnails;
import org.aspectj.lang.annotation.DeclareWarning;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
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

    public static String saveImage(MultipartFile file,
                            String imageDir,
                            String folder,
                            String fileName) throws IOException {
        Path uploadDirPath = Paths.get(imageDir, folder);
        Files.createDirectories(uploadDirPath);

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IOException("File upload không hợp lệ (không phải ảnh): " + file.getOriginalFilename());
        }

        // Luôn convert sang jpg
        String ext = "jpg";
        if (!fileName.endsWith("." + ext)) {
            fileName = fileName + "." + ext;
        }
        Path targetPath = uploadDirPath.resolve(fileName);

        // Resize 500x500 và lưu
        Thumbnails.of(image)
                .size(500, 500)
                .outputFormat(ext)
                .toFile(targetPath.toFile());

        return folder + "/" + fileName;
    }

}
