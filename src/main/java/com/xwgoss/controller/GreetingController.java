package com.xwgoss.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwgoss.service.DataService;
import com.xwgoss.util.ConfigUtil;

@RestController
@RequestMapping("/query")
public class GreetingController {

	@Autowired
	private DataService dataService;
	
	@RequestMapping("/currentData")
	public Map getCurrentData(@RequestParam String term){
		return dataService.getCurrentResult(term);
	}
	@RequestMapping("/currentTerm")
	public Map getCurrentTerm(){
		return dataService.getCurrentTerm();
	}
	@RequestMapping("/historyData")
	public Map getHistoryData(@RequestParam String term){
		return dataService.getHistoryResult(term);
	}
	@RequestMapping("/historyTerm")
	public Map getHistoryTerm(){
		return dataService.getCurrentTerm();
	}
}
