package com.vendor.management.system.application.util;

public class UrlHelper {
    private UrlHelper() {
    }

    public static class UserServiceUrl {
        public static final String ROOT = "/user";
        public static final String CREATE_VENDOR = "/vendor";
        public static final String CREATE_CUSTOMER = "/customer";
        public static final String CREATE_VENDOR_USER = "/vendor/user";
        public static final String CUSTOMERS_LIST = "/customers";
    }

    public static class StockServiceUrl {
        public static final String ROOT_PRODUCT_CATEGORY = "/stock/category";

        public static final String ROOT_PRODUCT = "/stock/product";

        public static final String ROOT_ORDER = "/stock/order";
        public static final String ORDER_LIST = "/list";
        public static final String ORDER_SETTLE = "/settle";
        public static final String ORDER_CANCEL = "/cancel";
        public static final String ORDER_REMOVE_PRODUCT = "/product";
        public static final String ORDER_REMOVE_ITEM = "/item";
    }
}
