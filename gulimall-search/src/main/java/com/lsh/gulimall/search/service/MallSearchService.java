package com.lsh.gulimall.search.service;

import com.lsh.gulimall.search.vo.SearchParam;
import com.lsh.gulimall.search.vo.SearchResult;

public interface MallSearchService {

	/**
	 * //TODO
	 *
	 * @param searchParam 检索参数
	 * @return
	 * @throws
	 * @date 2021/7/8 2:01
	 * @Description 返回检索结果
	 */
	SearchResult search(SearchParam searchParam);
}
