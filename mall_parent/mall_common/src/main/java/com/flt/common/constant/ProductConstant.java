package com.flt.common.constant;

public class ProductConstant {
    public enum AttrTypeEnum {
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");
        private final int code;
        private final String msg;

        AttrTypeEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    public enum StatusEnum {
        NEW_SPU(0, "新建"), SPU_UP(1, "上架"), SPU_DOWN(2, "下架");
        private final int code;
        private final String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
