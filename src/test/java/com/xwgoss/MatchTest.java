package com.xwgoss;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class MatchTest {

	@Test
	public void test() {
		String str=" INFO JSCloudBroker:105 to:formal_cloud_jcxt@openfire-cloud/jscsp_master serviceId：3c.park.queryparkplacelist 发送数据";
		String str1="{\"attributes\":{\"status\":0,\"CUSTOMER_ID\":\"880002901004606\",\"parkCode\":\"0000003628\"},\"dataItems\":[],\"requestType\":\"DIRECTIVE\",\"seqId\":\"-dtuq5d_1b0wv\",\"serviceId\":\"3c.park.queryparkplacelist\"}";
		String str2="2017-03-31 14:51:24  INFO S:? formal_cloud_jcxt@openfire-cloud/jscsp_master -2wb1l4_1hfci used ms: 286";
		Pattern p=Pattern.compile(" ms:(.[0-9]*)");
		Matcher m=p.matcher(str2);
		if(m.find())
			System.out.println(m.group(1));
	}
	
	@Test
	public void test1(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");
		Date date=new Date();
		System.out.println(sdf.format(date));
	}

	@Test
	public void test2(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			System.out.println(sdf.parse("2017-04-18 15:14:11").getHours());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
