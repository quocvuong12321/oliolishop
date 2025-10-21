package com.oliolishop.oliolishop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oliolishop.oliolishop.configuration.VNPayConfig;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    public String createOrder(int total, String orderInfor, String vnpTxnRef, String urlReturn) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
//        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);

        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total * 100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnpTxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

//        urlReturn += VNPayConfig.vnp_Returnurl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    public int orderReturn(HttpServletRequest request) {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
//        request.getParameterMap().forEach((key, values) -> {
//            // Chỉ lấy các param bắt đầu bằng "vnp_"
//            if (key.startsWith("vnp_") && values.length > 0) {
//                fields.put(key, values[0]);
//            }
//        });

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("transactionId");

        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }


    public String calculateRefundHash(Map<String, String> fields) {
        // Thứ tự các tham số cố định theo tài liệu VNPAY
        String data = String.join("|",
                fields.getOrDefault("vnp_RequestId", ""),
                fields.getOrDefault("vnp_Version", ""),
                fields.getOrDefault("vnp_Command", ""),
                fields.getOrDefault("vnp_TmnCode", ""),
                fields.getOrDefault("vnp_TransactionType", ""),
                fields.getOrDefault("vnp_TxnRef", ""),
                fields.getOrDefault("vnp_Amount", ""),
                fields.getOrDefault("vnp_TransactionNo", ""), // optional, nếu có
                fields.getOrDefault("vnp_TransactionDate", ""),
                fields.getOrDefault("vnp_CreateBy", ""),
                fields.getOrDefault("vnp_CreateDate", ""),
                fields.getOrDefault("vnp_IpAddr", ""),
                fields.getOrDefault("vnp_OrderInfo", "") // để cuối cùng nếu có
        );

        return VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, data);
    }

//    public String refundOrder(
//            int amount,
//            String vnpTxnRefMoi,
//            String createDateGoc,
//            String transactionNoGoc,
//            String refundType,
//            String vnpRequestIdMoi,
//            String orderInfo,
//            String vnpCreateBy) {
//
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "refund"; // Lệnh hoàn tiền
//        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
//        String vnp_IpAddr = "127.0.0.1"; // IP của Server gọi API
//
//        // 1. Chuẩn bị các tham số cho VNPAY
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", vnp_Version);
//        vnp_Params.put("vnp_Command", vnp_Command);
//        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//        vnp_Params.put("vnp_TxnRef", vnpTxnRefMoi); // Mã tham chiếu của lệnh hoàn tiền MỚI
//        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Số tiền hoàn (nhân 100)
//        vnp_Params.put("vnp_OrderInfo", orderInfo); // Lý do hoặc thông tin hoàn tiền
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//        vnp_Params.put("vnp_RequestId", vnpRequestIdMoi); // Mã định danh yêu cầu REFUND
//
//        // 2. Tham số bắt buộc cho lệnh hoàn tiền
//        vnp_Params.put("vnp_TransactionNo", transactionNoGoc); // Mã giao dịch VNPAY GỐC
//        vnp_Params.put("vnp_TransactionType", refundType); // Loại hoàn tiền: Full (02)/Partial (03)
//        vnp_Params.put("vnp_TransactionDate", createDateGoc); // Ngày giao dịch GỐC (yyyyMMddHHmmss)
//
//        // 3. Ngày tạo lệnh hoàn tiền (là thời điểm hiện tại)
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//
//        // 4. Người tạo lệnh hoàn tiền
//        vnp_Params.put("vnp_CreateBy", vnpCreateBy);
//
//        // 5. TÍNH TOÁN HASH BẰNG QUY TẮC PIPE-SEPARATED CỐ ĐỊNH CHO REQUEST
//        String vnp_SecureHash = calculateRefundRequestHash(vnp_Params);
//
//        // 6. Sắp xếp các tham số để tạo Query String (vẫn cần để parse ở executeVnPayRefundPost)
//        List fieldNames = new ArrayList(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//
//        StringBuilder query = new StringBuilder();
//        Iterator itr = fieldNames.iterator();
//        while (itr.hasNext()) {
//            String fieldName = (String) itr.next();
//            String fieldValue = (String) vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
//                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=');
//                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
//                if (itr.hasNext()) {
//                    query.append('&');
//                }
//            }
//        }
//
//        String queryUrl = query.toString();
//        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//
//        return VNPayConfig.vnp_apiUrl + "?" + queryUrl;
//    }

    public String refundOrder(
            int amount,
            String vnpTxnRefMoi,       // Mã tham chiếu lệnh hoàn tiền
            String createDateGoc,      // Ngày giao dịch gốc (yyyyMMddHHmmss)
            String transactionNoGoc,   // Mã giao dịch VNPAY gốc
            String refundType,         // "02" full refund / "03" partial
            String vnpRequestIdMoi,    // UUID cho refund request
            String orderInfo,
            String vnpCreateBy
    ) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_IpAddr = "127.0.0.1";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TxnRef", vnpTxnRefMoi);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_RequestId", vnpRequestIdMoi);
        vnp_Params.put("vnp_TransactionType", refundType);
        vnp_Params.put("vnp_TransactionDate", createDateGoc);
        vnp_Params.put("vnp_CreateBy", vnpCreateBy);

        // Ngày tạo lệnh refund hiện tại
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Gán mã giao dịch gốc VNPAY
        if (transactionNoGoc != null && !transactionNoGoc.isEmpty()) {
            vnp_Params.put("vnp_TransactionNo", transactionNoGoc);
        }


//        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, data);
        String vnp_SecureHash = calculateRefundHash(vnp_Params);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        // Chuyển Map sang Query String để gọi POST
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                        .append("&");
            }
        }
        // Xóa ký tự & cuối cùng
        if (query.length() > 0) query.setLength(query.length() - 1);

        return VNPayConfig.vnp_apiUrl + "?" + query.toString();
    }

    private String calculateResponseHash(Map<String, String> fields) {
        // Quy tắc Hash BẮT BUỘC cho VNPAY Response

        String data = fields.getOrDefault("vnp_ResponseId", "") + "|" +
                fields.getOrDefault("vnp_Command", "") + "|" +
                fields.getOrDefault("vnp_ResponseCode", "") + "|" +
                fields.getOrDefault("vnp_Message", "") + "|" +
                fields.getOrDefault("vnp_TmnCode", "") + "|" +
                fields.getOrDefault("vnp_TxnRef", "") + "|" +
                fields.getOrDefault("vnp_Amount", "") + "|" +
                fields.getOrDefault("vnp_BankCode", "") + "|" +
                fields.getOrDefault("vnp_PayDate", "") + "|" +
                fields.getOrDefault("vnp_TransactionNo", "") + "|" +
                fields.getOrDefault("vnp_TransactionType", "") + "|" +
                fields.getOrDefault("vnp_TransactionStatus", "") + "|" +
                fields.getOrDefault("vnp_OrderInfo", "");

        return VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, data);
    }


    public int refundReturn(String vnpResponseQueryString) {
        Map<String, String> fields;
        try {
            fields = parseQueryStringToMap(vnpResponseQueryString);
        } catch (Exception e) {
            e.printStackTrace();
            return -2; // Lỗi khi phân tích chuỗi
        }

        String vnp_SecureHash = fields.get("vnp_SecureHash");

        if (vnp_SecureHash == null) {
            return -3; // Không tìm thấy Secure Hash
        }

        // Chuẩn bị Map cho Hash
        Map<String, String> fieldsForHash = new HashMap<>(fields);
        fieldsForHash.remove("vnp_SecureHash");
        fieldsForHash.remove("vnp_SecureHashType"); // Loại bỏ nếu có

        // Xác thực Hash bằng quy tắc cố định (pipe-separated)
        String signValue = calculateResponseHash(fieldsForHash);

        if (signValue.equals(vnp_SecureHash)) {
            String vnp_ResponseCode = fields.get("vnp_ResponseCode");
            String vnp_TransactionStatus = fields.get("vnp_TransactionStatus");

            if ("00".equals(vnp_ResponseCode)) {
                // Giao dịch thành công, kiểm tra trạng thái
                switch (vnp_TransactionStatus) {
                    case "00":
                        return 1; // Hoàn tiền thành công
                    case "05":
                    case "06":
                        return 2; // GD hoàn tiền đang xử lý
                    case "09":
                        return 0; // GD hoàn trả bị từ chối
                    default:
                        return 3; // Các trạng thái khác (chưa hoàn tất, lỗi, nghi ngờ gian lận, ...)
                }
            } else {
                return 0; // GD thất bại (dữ liệu hợp lệ nhưng bị từ chối/lỗi)
            }
        } else {
            return -1; // Chữ ký không hợp lệ (dữ liệu bị giả mạo)
        }
    }

    private Map<String, String> parseQueryStringToMap(String queryString) throws Exception {
        Map<String, String> map = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) return map;

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                map.put(
                        URLDecoder.decode(key, StandardCharsets.US_ASCII.toString()),
                        URLDecoder.decode(value, StandardCharsets.US_ASCII.toString())
                );
            }
        }
        return map;
    }


    public String executeVnPayRefundPost(String refundApiUrlWithQuery) {
        // 1. Tách Base URL và Query String
        String baseUrl = refundApiUrlWithQuery;
        String queryString = "";
        int queryStart = refundApiUrlWithQuery.indexOf('?');
        if (queryStart > 0) {
            baseUrl = refundApiUrlWithQuery.substring(0, queryStart);
            queryString = refundApiUrlWithQuery.substring(queryStart + 1);
        }

        try {
            // 2. Chuyển Query String (chứa tham số VNPAY) thành Map<String, String>
            Map<String, String> stringParamsMap = parseQueryStringToMap(queryString);

            // 2.1. Chuẩn bị Map<String, Object> cho JSON serialization
            Map<String, Object> vnpParamsMap = new LinkedHashMap<>();

            for (Map.Entry<String, String> entry : stringParamsMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

//                // CHUYỂN CÁC TRƯỜNG CÓ DỮ LIỆU SỐ SANG LONG (JSON Number)
////                 vnp_Amount, vnp_CreateDate, vnp_TransactionDate là các trường số cố định
                if (
                        key.equals("vnp_Amount")
                                || key.equals("vnp_CreateDate")
                                || key.equals("vnp_TransactionDate")
//                        || key.equals("vnp_TransactionType")
                                || key.equals("vnp_TransactionNo")
                ) {
                    try {
                        vnpParamsMap.put(key, Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        vnpParamsMap.put(key, value);
                        System.err.println("Lỗi parse Long cho trường " + key + ", giá trị: " + value);
                    }
                } else {
                    // Giữ lại vnp_TransactionNo và vnp_TransactionType là String để tránh lỗi format
                    vnpParamsMap.put(key, value);
                }
            }

            // **********************************************
            List<String> requiredFields = List.of(
                    "vnp_RequestId", "vnp_Version", "vnp_Command", "vnp_TmnCode",
                    "vnp_TransactionType", "vnp_TxnRef", "vnp_Amount", "vnp_OrderInfo",
                    "vnp_TransactionDate", "vnp_CreateBy", "vnp_CreateDate", "vnp_IpAddr", "vnp_SecureHash"
            );
            for (String field : requiredFields) {
                if (!vnpParamsMap.containsKey(field)) {
                    System.out.println("Thiếu field");
                    throw new AppException(ErrorCode.PAYMENT_INVALID);
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(vnpParamsMap);

            System.out.println("=== VNPAY REFUND REQUEST ===");
            System.out.println("POST " + baseUrl);
            System.out.println(jsonPayload);

            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseString = response.toString();
            System.out.println("=== VNPAY REFUND RESPONSE ===");
            System.out.println(responseString);

            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
                throw new AppException(ErrorCode.PAYMENT_INVALID);
            }

            // Trả về dạng query string tương thích refundReturn
            Map<String, Object> jsonResponseMap = objectMapper.readValue(responseString, Map.class);

            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, Object> e : jsonResponseMap.entrySet()) {
                if (e.getValue() != null) {
                    query.append(URLEncoder.encode(e.getKey(), StandardCharsets.US_ASCII))
                            .append('=')
                            .append(URLEncoder.encode(e.getValue().toString(), StandardCharsets.US_ASCII))
                            .append('&');
                }
            }

            return query.length() > 0 ? query.substring(0, query.length() - 1) : "";

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.PAYMENT_INVALID);
        }
    }
}
