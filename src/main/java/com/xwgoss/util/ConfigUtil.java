package com.xwgoss.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil {
	private ConfigUtil(){
	}
	private Map<String,Properties> proMap;
	private Map<String,int[]> currentResult;
	private Map<String,int[]> historyResult;
	private static ConfigUtil cu;
	public void setCurrentResult(Map<String,int[]> result){
		this.currentResult=result;
	}
	public Map<String,int[]> getCurrentResult(){
		return currentResult;
	}
	public synchronized static ConfigUtil getInstance(){
		if(cu==null)
			cu=new ConfigUtil();
		return cu;
	}
	public void putPro(String name,Properties pro){
		if(proMap==null)
			proMap=new HashMap<String,Properties>();
		proMap.put(name, pro);
	}
	public Properties getPro(String name){
		if(proMap==null)
			proMap=new HashMap<String,Properties>();
		return proMap.get(name);
	}
	public Map<String, int[]> getHistoryResult() {
		return historyResult;
	}
	public void setHistoryResult(Map<String, int[]> historyResult) {
		this.historyResult = historyResult;
	}
	
	
}
