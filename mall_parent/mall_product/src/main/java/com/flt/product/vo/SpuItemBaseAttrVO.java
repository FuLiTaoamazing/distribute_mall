package com.flt.product.vo;

import lombok.Data;

import java.util.List;
@Data
public class SpuItemBaseAttrVO {
    private String groupName;
    private List<Attr> attrs;
}
