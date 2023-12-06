package com.lsh.gulimall.search.web;

import com.lsh.gulimall.search.service.MallSearchService;
import com.lsh.gulimall.search.vo.SearchParam;
import com.lsh.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
@Slf4j
public class SearchController {

	@Autowired
	MallSearchService mallSearchService;

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/8 22:48
	 * @Description 检索查询
	 */
	@GetMapping(value = {"/", "/list.html"})
	public String listPage(SearchParam searchParam, Model model, HttpServletRequest request) {

		String queryString = request.getQueryString();
		searchParam.set_queryString(queryString);
		SearchResult result = mallSearchService.search(searchParam);
		log.warn("搜索产品--------------------"+result);

		model.addAttribute("result", result);

		return "list";
	}
}
