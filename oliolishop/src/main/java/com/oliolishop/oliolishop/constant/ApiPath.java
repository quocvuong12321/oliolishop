package com.oliolishop.oliolishop.constant;

import java.util.HashMap;
import java.util.Map;

public class ApiPath {
    public static final String BASE = "/api";
    public static final String BY_ID = "/{id}";

    //Folder lưu ảnh attribute
    public static final String FOLDER_IMAGE_ATTR = "images_attr";


    //Endpoint cho api
    public static final String SPU = "/spu";

    public static final String SKU = "/sku";

    public static final String SKU_ATTR = "/sku-attr";

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
    }

    public static final class Customer{
        public static final String ROOT = BASE+"/customer/address";
    }
}
