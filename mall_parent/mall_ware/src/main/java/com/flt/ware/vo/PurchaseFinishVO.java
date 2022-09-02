package com.flt.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseFinishVO {

    private Long id;
    private List<PurchaseDoneItemVO> items;
}
