package com.xwgoss.service;

import java.util.Map;

public interface DataService {
	public Map<String,int[]> getCurrentResult(String term);
	public Map<String,String> getCurrentTerm();
	public Map<String,int[]> getHistoryResult(String term);
	public Map<String,String> getHistoryTerm();
}
