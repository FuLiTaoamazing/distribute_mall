package com.flt.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2VO {
    private String catalog1Id;
    private List<Catelog3VO> catalog3List;
    private String id;
    private String name;
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3VO{
        private String catalog2Id;
        private String id;
        private String name;

    }
}
