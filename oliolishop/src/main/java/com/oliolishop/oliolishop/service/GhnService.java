package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.ghn.GhnPreviewRequest;
import com.oliolishop.oliolishop.dto.ghn.GhnPreviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GhnService {

    private RestTemplate createUnsafeRestTemplate() {
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


    private final RestTemplate restTemplate = createUnsafeRestTemplate();
    @Value("${ghn.token}")
    String ghnToken;

    @Value("${ghn.shop-id}")
    String shopId;

    @Value("${ghn.from.ward-name}")
    String fromWardName;
    @Value("${ghn.from.district-name}")
    String fromDistrictName;
    @Value("${ghn.from.province-name}")
    String fromProvinceName;
    @Value(("${ghn.from.detail-address}"))
    String detailAddress;

    private static final String PREVIEW_URL =
            "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/preview";

    public static final String CREATE_ORDER_URL =
            "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create";

    public GhnPreviewResponse getPreview(GhnPreviewRequest request) {

        request.setFrom_address(detailAddress);
        request.setFrom_province_name(fromProvinceName);
        request.setFrom_district_name(fromDistrictName);
        request.setFrom_ward_name(fromWardName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", ghnToken);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GhnPreviewRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GhnPreviewResponse> response = restTemplate
                .postForEntity(PREVIEW_URL, entity, GhnPreviewResponse.class);

        return response.getBody();
    }

    public GhnPreviewResponse createOrder(GhnPreviewRequest request){
        request.setFrom_address(detailAddress);
        request.setFrom_province_name(fromProvinceName);
        request.setFrom_district_name(fromDistrictName);
        request.setFrom_ward_name(fromWardName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("token",ghnToken);
        headers.set("ShopId",shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GhnPreviewRequest> entity = new HttpEntity<>(request,headers);

        ResponseEntity<GhnPreviewResponse> response = restTemplate
                .postForEntity(CREATE_ORDER_URL,entity,GhnPreviewResponse.class);
        return response.getBody();
    }


}