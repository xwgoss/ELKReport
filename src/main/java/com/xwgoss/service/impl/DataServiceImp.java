package com.xwgoss.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.xwgoss.service.DataService;
import com.xwgoss.util.ConfigUtil;
@Component
public class DataServiceImp implements DataService {

	@Override
	public Map<String, int[]> getCurrentResult(String term) {
		// TODO Auto-generated method stub
		if(term.equals("all"))
			{
			Map<String,int[]> map=new HashMap<String,int[]>();
			for(String str:ConfigUtil.getInstance().getCurrentResult().keySet())
				map.put(ConfigUtil.getInstance().getPro("commands").getProperty(str,str), ConfigUtil.getInstance().getCurrentResult().get(str));
			return map;
			}
		
		else
			{
				Map<String,int[]> map=new HashMap<String,int[]>();
				for(String str:term.split(","))
					map.put(ConfigUtil.getInstance().getPro("commands").getProperty(str,str), ConfigUtil.getInstance().getCurrentResult().get(str));
				return map;
			}
	}

	@Override
	public Map<String, String> getCurrentTerm() {
		// TODO Auto-generated method stub
		if(ConfigUtil.getInstance().getCurrentResult()!=null){
			Map<String,String> map=new HashMap<String,String>();
			for(String str:ConfigUtil.getInstance().getCurrentResult().keySet()){
				map.put(str, ConfigUtil.getInstance().getPro("commands").getProperty(str,str));
			}
			return map;
		}
		return null;
	}

	@Override
	public Map<String, int[]> getHistoryResult(String term) {
		// TODO Auto-generated method stub
		if(term.equals("all"))
		{
		Map<String,int[]> map=new HashMap<String,int[]>();
		for(String str:ConfigUtil.getInstance().getHistoryResult().keySet())
			map.put(ConfigUtil.getInstance().getPro("commands").getProperty(str,str), ConfigUtil.getInstance().getHistoryResult().get(str));
		return map;
		}
	
	else
		{
			Map<String,int[]> map=new HashMap<String,int[]>();
			for(String str:term.split(","))
				map.put(ConfigUtil.getInstance().getPro("commands").getProperty(str,str), ConfigUtil.getInstance().getHistoryResult().get(str));
			return map;
		}
	}

	@Override
	public Map<String, String> getHistoryTerm() {
		// TODO Auto-generated method stub
		if(ConfigUtil.getInstance().getHistoryResult()!=null){
			Map<String,String> map=new HashMap<String,String>();
			for(String str:ConfigUtil.getInstance().getHistoryResult().keySet()){
				map.put(str, ConfigUtil.getInstance().getPro("commands").getProperty(str,str));
			}
			return map;
		}
		return null;
	}

	
}
