package com.xwgoss.service;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.xwgoss.util.ConfigUtil;

public class CoreService implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger=Logger.getLogger(CoreService.class);
	private ScheduledExecutorService currentThreadPool;
	private ScheduledExecutorService historyThreadPool;
	private String elasticsearchIp;
	private int elasticsearchPort;
	private int time;
	private int currentPeriod;
	private String historyStartTime;
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		// TODO Auto-generated method stub
		 Properties pro=new Properties();
		 Properties pro_jsaims=new Properties();
		 Properties commands=new Properties();
		 try {
			pro.load(arg0.getApplicationContext().getResource("classpath:/application.properties").getInputStream());
			pro_jsaims.load(arg0.getApplicationContext().getResource("classpath:/pro_jsaims.properties").getInputStream());
			commands.load(arg0.getApplicationContext().getResource("classpath:/command.properties").getInputStream());
			ConfigUtil.getInstance().putPro("pro_jsaims", pro_jsaims);
			ConfigUtil.getInstance().putPro("config", pro);
			ConfigUtil.getInstance().putPro("commands", convertUTF8(commands));
			init(pro);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stubHistoryLogHandler1
					currentThreadPool.scheduleWithFixedDelay(new CurrentLogHandler(elasticsearchIp,elasticsearchPort,getQuery(time)), 0, currentPeriod, TimeUnit.MINUTES);
					historyThreadPool.scheduleWithFixedDelay(new HistoryLogHandler(elasticsearchIp,elasticsearchPort,historyStartTime,getQuery()), 0, 1, TimeUnit.MINUTES);
					logger.info("服务启动成功");
					
				}
			}).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("程序启动时,读取配置文件报错",e.fillInStackTrace());
		}
				 
	}
	private Properties convertUTF8(Properties commands) {
		// TODO Auto-generated method stub
		for(Object o:commands.keySet()){
			try {
				commands.put(o, new String(commands.getProperty(o.toString()).getBytes("ISO-8859-1"),"utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error("转换utf8时报错",e.fillInStackTrace());
			}
		}
		return commands;
	}
	//初始化线程组和相关配置
	private void init(Properties pro){
		currentThreadPool=Executors.newScheduledThreadPool(Integer.valueOf(pro.getProperty("report.current.thread","1"))+1);
		historyThreadPool=Executors.newScheduledThreadPool(Integer.valueOf(pro.getProperty("report.history.thread","1"))+1);
		elasticsearchIp=pro.getProperty("elasticsearch.ip");
		elasticsearchPort=Integer.valueOf(pro.getProperty("elasticsearch.port"));
		time=Integer.valueOf(pro.getProperty("report.current.timeScope","5"));
		currentPeriod=Integer.valueOf(pro.getProperty("report.current.period","5"));
		historyStartTime=pro.getProperty("report.history.period","20:12");
	}

	private QueryBuilder getQuery(int time){
		Properties pro=ConfigUtil.getInstance().getPro("pro_jsaims");
		BoolQueryBuilder bqb=new BoolQueryBuilder();
		for(Object o:pro.keySet()){
			bqb.must(getSingleQuery(o.toString(), pro.getProperty(o.toString())));
		}
			Date date=new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MINUTE, -time);
			Date date1 = calendar.getTime();
		return bqb.must(rangeQuery("@timestamp").gte(date1.getTime()).lte(date.getTime()));
	}
	
	private QueryBuilder getQuery(){
		Properties pro=ConfigUtil.getInstance().getPro("pro_jsaims");
		BoolQueryBuilder bqb=new BoolQueryBuilder();
		for(Object o:pro.keySet()){
			bqb.must(getSingleQuery(o.toString(), pro.getProperty(o.toString())));
		}
		return bqb;
	}
	
	private QueryBuilder getSingleQuery(String key,String content){
		BoolQueryBuilder bqb=new BoolQueryBuilder();
		if(content.contains(")|(")){
			String[] tempContent=content.split("\\)|\\(");
			for(String temp:tempContent){
				bqb.should(getSingleQuery(key, temp)).minimumShouldMatch("75%");
			}
			return bqb;
		}else if(content.contains(")&(")){
			String[] tempContent=content.split(")&(");
			for(String temp:tempContent){
				bqb.must(getSingleQuery(key, temp));
			}
			return bqb;
		}else if(content.contains("|")){
			String[] tempContent=content.split("\\|");
			for(String temp:tempContent){
				bqb.should(getTermOrRegxQuery(key, temp));
			}
			return bqb;
		}else if(content.contains("&")){
			String[] tempContent=content.split("\\&");
			for(String temp:tempContent){
				bqb.must(getTermOrRegxQuery(key, temp));
			}
			return bqb;
		}
		else{
			return getTermOrRegxQuery(key, content); 
		}
	}
	
	private QueryBuilder getTermOrRegxQuery(String key,String content){
		if(content.contains("*"))
		return regexpQuery(key, content);
		else
		return termQuery(key, content);
	} 
}
