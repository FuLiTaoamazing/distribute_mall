package com.flt.product.feign;

import com.flt.common.to.es.SkuEsModeTO;
import com.flt.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("MALL-SEARCH")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
     R productUp(@RequestBody List<SkuEsModeTO> skuEsModeTOList);
}
