package com.mmall.common;

import org.omg.PortableInterceptor.INACTIVE;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";

    public interface Role{
        int ROLE_CUSTOMER = 0;//customer
        int ROLE_ADDMIN = 1;//admin
    }

    public interface Cart{
        int CHECKED = 1;
        int UNCHECKED = 0;

        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";

    }

    public enum ProductStatusEnums{
        ON_SALE(1,"在线");

        private String value;
        private int code;

        ProductStatusEnums(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
