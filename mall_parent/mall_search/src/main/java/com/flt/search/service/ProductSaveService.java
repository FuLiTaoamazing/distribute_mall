package com.flt.search.service;

import com.flt.common.to.es.SkuEsModeTO;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {

     boolean productUp(List<SkuEsModeTO> skuEsModeTOList) throws IOException;
}
