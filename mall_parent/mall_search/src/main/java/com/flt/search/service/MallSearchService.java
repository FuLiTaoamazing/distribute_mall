package com.flt.search.service;

import com.flt.search.vo.SearchParamsVO;
import com.flt.search.vo.SearchResultVO;

public interface MallSearchService {

    SearchResultVO search(SearchParamsVO vo);
}
