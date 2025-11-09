package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.ghn.GhnPreviewRequest;
import com.oliolishop.oliolishop.dto.ghn.GhnPreviewResponse;
import com.oliolishop.oliolishop.util.AppUtils;
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




    private final RestTemplate restTemplate = AppUtils.createUnsafeRestTemplate();
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