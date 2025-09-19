package com.oliolishop.oliolishop.ultils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.DeclareWarning;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AppUtils {
    public static String toSlug(String input){
//        chuẩn hóa chuỗi Unicode
//        \p{M}: trong regex Unicode nghĩa là Mark → ký tự dấu kết hợp (combining marks), ví dụ:
//        á sau khi normalize (NFD) sẽ thành a + ́ (ký tự dấu sắc). \p{M} bắt cái ́ đó và xóa đi, chỉ để lại a.
        String normalize = Normalizer.normalize(input,Normalizer.Form.NFD);

        //Xóa dấu tiếng Việt
        String noDiacritics = normalize.replaceAll("\\p{M}", "");

        //Chuyển thường
        String lower = noDiacritics.toLowerCase();

        //thay ký tự không phải a-z,0-9 thành dấu gạch ngang
//        [^a-z0-9] nghĩa là bất kỳ ký tự nào KHÔNG nằm trong a–z hoặc 0–9.
//         + nghĩa là một hoặc nhiều ký tự liên tiếp.
        String slug = lower.replaceAll("[^a-z0-9]+","-");

        //Loại bỏ gạch ngang đầu cuối
//        ^ nghĩa là đầu chuỗi.
//        $ nghĩa là cuối chuỗi.
//        (^-|-$) nghĩa là nếu ở đầu chuỗi có dấu - hoặc ở cuối chuỗi có dấu - thì bỏ đi.
        return slug.replaceAll("(^-|-$)","");
    }

    public static String convertToURL(String slug, String id){
        return  slug+"/c"+id;
    }

    public static String convertToSpuUrl(String slug, String spu_id){
        return slug+"/p"+spu_id;
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

}
