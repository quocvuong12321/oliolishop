package com.oliolishop.oliolishop.constant;

import com.oliolishop.oliolishop.entity.Rating;

import java.util.HashMap;
import java.util.Map;

public class ApiPath {


    public static final String BASE = "/api";
    public static final String FULLURL = "http://localhost:8080/oliolishop";

    public static final String BY_ID = "/{id}";
    //Folder lưu ảnh
    public static final String FOLDER_IMAGE_ATTR = "images_attr";
    public static final String FOLDER_IMAGE_AVATAR = "images_avatar";
    public static final String FOLDER_IMAGE_RATING = "images_rating";


    //Endpoint cho api
    public static final class Spu{
        public static final String ROOT = BASE + "/spu";
        public static final String DETAIL = "/detail"+BY_ID;
        public static final String RATING = DETAIL+"/ratings";
        public static final String LIKE_RATING = RATING+"/like";
    }


    public static final class ProductSku{
        public static final String ROOT = BASE +"/sku";
    }

    public static final class SkuAttr{
        public static final String ROOT = BASE + "/sku-attr";
    }


    public static final String CATEGORY = "/category";

    public static final String BRAND = "/brand";

    public static final String IMAGE = "/image";

    public static final String ACCOUNT = "/account";

    public static final String AUTHENTICATION = "/auth";

    public static final String DISCOUNT_RULE= "/discount-rule";

    public static final String CART="/cart";

    public static final class Employee {
        public static final String ROOT = BASE + "/employee";
        public static final String LOGIN = "/login";
        public static final String PROFILE = "/profile";
        public static final String CREATE = "/create";
        public static final String UPDATE = "/update";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
    }

    public static final class Customer{
        public static final String ROOT = BASE+"/customer";
        public static final String ADDRESS = "/address";
    }

    public static final class Order{
        public static final String ROOT = BASE+"/order";
        public static final String CONFIRM = "/confirm";
        public static final String RATING = "/rating";
        public static final String CANCEL_ORDER = "/cancel-order";
        public static final String CHECK_OUT  = "/check-out";
    }

    public static final class Payment{
        public static final String ROOT = BASE+"/payment";
        public static final String VNPAY = "/vnpay";
        public static final String VNPAY_RETURN = VNPAY+"/return";
    }

    public static final class Location{
        public static final String ROOT = BASE+"/locations";
        public static final String PROVINCES = "/provinces";
        public static final String DISTRICTS_BY_PROVINCE = "/provinces/{provinceId}/districts";
        public static final String WARDS_BY_DISTRICT = "/districts/{districtId}/wards";
    }

    public static final class Voucher{
        public static final String ROOT = BASE+"/voucher";
    }

}
