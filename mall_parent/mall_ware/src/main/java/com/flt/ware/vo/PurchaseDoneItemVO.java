package com.flt.ware.vo;

import lombok.Data;

@Data
public class PurchaseDoneItemVO {
    private Long itemId;

    private Integer status;

    private String reason;
}
