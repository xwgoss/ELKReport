package com.xwgoss.service;

import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import static org.elasticsearch.index.query.QueryBuilders.*;

import com.xwgoss.bean.CommandBean;
import com.xwgoss.util.ConfigUtil;

public class CurrentLogHandler implements Runnable{
	private static final Logger logger=Logger.getLogger(CurrentLogHandler.class);
	private TransportClient client;
	private QueryBuilder qb;
	private final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat file_sdf=new SimpleDateFormat("yyyy.MM.dd");
	//yyyy-MM-dd’T'HH:mm:ss.SSSZZ
	private final SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	
	private final SimpleDateFormat m_sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public CurrentLogHandler(String ip,int port,QueryBuilder qb){
		client = new PreBuiltTransportClient(Settings.EMPTY);
		try {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip),port));
			//初始化查询条件
			this.qb=qb;
		} catch (UnknownHostException e) {
			logger.error("连接elasticsearch时发生错误",e.fillInStackTrace());
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("实时日志抓取开始执行了……");
		try{
		int i=0;
		int m_current=new Date().getMinutes();
		Map<String,CommandBean> map=new HashMap<String,CommandBean>();
		Map<String,List<CommandBean>> map1=new HashMap<String,List<CommandBean>>();
		SearchResponse  scrollResp= client.prepareSearch("filebeat-"+file_sdf.format(new Date())).setTypes("log").setScroll(new TimeValue(30000)).
				setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb).setSize(1000).get();
		do{
			for(SearchHit h:scrollResp.getHits()){
//				System.out.println(h.getSource().get("message").toString());
				CommandBean cb=saveData(h.getSource().get("message").toString());
				if(cb.getSeqId()==null)
					continue;
				if(map.containsKey(cb.getSeqId())){
					try {
						i++;
						String serviceId=cb.getServiceId()==null?map.get(cb.getSeqId()).getServiceId():cb.getServiceId();
						Long l=cb.getTimeSpent()==null?map.get(cb.getSeqId()).getTimeSpent():cb.getTimeSpent();
						if(l==null)
						cb.setTimeSpent(sumSpentTime(cb.getInTime(),map.get(cb.getSeqId()).getInTime()));
						else
						cb.setTimeSpent(l);
						if(map1.containsKey(serviceId))
							map1.get(serviceId).add(cb);
						else{
							List<CommandBean> cbs=new ArrayList<CommandBean>();
							cbs.add(cb);
							map1.put(serviceId, cbs);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						logger.error("时间转换时报错,相关信息:"+cb.toString(), e.fillInStackTrace());
					}
				}else
					map.put(cb.getSeqId(), cb);
			}
			 scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
			 logger.info("循环查找中");
		}while(scrollResp.getHits().getHits().length != 0);
		logger.info("该实时范围内共访问指令数："+i);
		ConfigUtil.getInstance().setCurrentResult(convertServiceIpReport(map1,m_current));
		}catch (Exception e) {
			logger.error("实时抓取日志线程发生异常",e.fillInStackTrace());
		}
	}
	private Map<String,int[]> convertServiceIpReport(Map<String,List<CommandBean>> map,int m_current){
		Map<String,int[]> report=new HashMap<String,int[]>();
		for(String key:map.keySet()){
			Map<Integer,List<Long>> tempMap=new HashMap<Integer,List<Long>>();
			for(int i=0;i<map.get(key).size();i++){
				int m;
				try {
					m = m_sdf.parse(map.get(key).get(i).getInTime()).getMinutes();
					if(tempMap.containsKey(m))
						tempMap.get(m).add(map.get(key).get(i).getTimeSpent());
					else{
						List<Long> l=new ArrayList<Long>();
						l.add(map.get(key).get(i).getTimeSpent());
						tempMap.put(m, l);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("转换时间时发生错误", e.fillInStackTrace());
				}
			}
			int[] sum=new int[tempMap.size()];
			int j=0;
			for(Integer i:tempMap.keySet()){
				Long tempSum=0l;
				for(Long l:tempMap.get(i))
					tempSum=tempSum+l;
				sum[j]=(int)(tempSum/tempMap.get(i).size());
				j++;
			}
			report.put(key,sum);
		}
		
		return report;
	}
	private Long sumSpentTime(String start,String end) throws ParseException{
		Long startTime=sdf.parse(start).getTime();
		Long endTime=sdf.parse(end).getTime();
		return Math.abs(endTime-startTime);
	}
	private CommandBean saveData(String str){
		CommandBean cb=new CommandBean();
		cb.setSeqId(getSeqId(str));
		cb.setServiceId(getServiceId(str));
		cb.setInTime(getTime(str));
		cb.setTimeSpent(getSpentTime(str));
		return cb;
			
	}
	private Long getSpentTime(String str) {
		// TODO Auto-generated method stub
		Pattern serviceId_P=Pattern.compile(ConfigUtil.getInstance().getPro("config").getProperty("spendTime.match"));
		Matcher m=serviceId_P.matcher(str);
		if(m.find())
			{
				return Long.valueOf(m.group(1).trim());
			}
		return null;
	}

	private String getServiceId(String str){
		Pattern serviceId_P=Pattern.compile(ConfigUtil.getInstance().getPro("config").getProperty("serviceId.match","serviceId：(.*) "));
		Matcher m=serviceId_P.matcher(str);
		if(m.find())
			{
				return m.group(1);
			}
		else{
//			logger.error(str+"=>无法发现ServiceId");
		}
			return null;
	}
	private String getSeqId(String str){
		String p1=null;
		String p2=null;
		if(ConfigUtil.getInstance().getPro("config").getProperty("seqId.match").contains(")or(")){
			p1=ConfigUtil.getInstance().getPro("config").getProperty("seqId.match").split("\\)or\\(")[0].substring(1);
			p2=ConfigUtil.getInstance().getPro("config").getProperty("seqId.match").split("\\)or\\(")[1];
			p2=p2.substring(0, p2.length()-1);
		}else{
			p1=ConfigUtil.getInstance().getPro("config").getProperty("seqId.match");
		}
		Pattern seqId=Pattern.compile(p1);
		Matcher m=seqId.matcher(str);
		if(m.find())
			return m.group(1);
		if(p2!=null){
			Pattern seqId2=Pattern.compile(p2);
			Matcher m2=seqId2.matcher(str);
			if(m2.find())
				{
					return m2.group(1);
				}
		}
		else{
			logger.error(str+"=>无法发现SeqId");
		}
			return null;
	}
	private String getTime(String str){
		return str.substring(0,19);
	}
	
}
