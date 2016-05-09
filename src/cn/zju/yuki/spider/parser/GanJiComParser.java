package cn.zju.yuki.spider.parser;

import house.Community;
import house.GanJi;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;

public class GanJiComParser {
	public Community parse(FetchedPage fetchedPage){
		Community community=new Community();
		Document doc = Jsoup.parse(fetchedPage.getContent());
		community.url=fetchedPage.getUrl();

		if(fetchedPage.getStatusCode()==200){
			//小区名
			if(!doc.getElementsByAttributeValue("class", "xiaoqu-title").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				community.communityName=doc.getElementsByAttributeValue("class", "xiaoqu-title").text().trim();
			}
			
			//小区详情
			if(!doc.getElementsByAttributeValue("class", "basic-info-ul").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "basic-info-ul").text().trim();
//				String regEx="小区均价：([0-9]*) 元/㎡.*二  手  房：([0-9]*) 套 出  租  房：([0-9]*) 套 所在区域：(.*) 详细地址：(.*)）查看地图>> 竣工时间：(.*) 物业类型：(.*) 开  发  商：(.*) 物业公司：(.*)";
////				String regEx="[^u4e00-u9fa5]";
//				String string="小区均价：7195 元/㎡ 环比上月：2%查看房价走势>> ";
//				String reg="[u4e00-u9fa5].[：]{1}([0-9]*)[u4e00-u9fa5]{1}/.*[u4e00-u9fa5]*[：]{1}.*";
//				Pattern pattern=Pattern.compile(reg);
//				Matcher matcher=pattern.matcher(string);
//				System.out.println(temp);
				int index1=temp.indexOf("二  手  房：");
				int index2=temp.indexOf("出  租  房：");
				int index3=temp.indexOf("所在区域：");
				int index4=temp.indexOf("详细地址：");
				int index5=temp.indexOf("竣工时间：");
				int index6=temp.indexOf("物业类型：");
				int index7=temp.indexOf("开  发  商：");
				int index8=temp.indexOf("物业公司：");
				community.erhouseNum=Integer.parseInt(temp.substring(index1+8, index2).replace("套", "").replaceAll(" ", "").trim());
				community.houseNum=Integer.parseInt(temp.substring(index2+8, index3).replace("套", "").replaceAll(" ", "").trim());
				community.communityLocation=temp.substring(index3+5, index4).trim();
				community.detailLocation=temp.substring(index4+5, index5).replace("查看地图>>", "").trim();
				community.finishTime=temp.substring(index5+5, index6).trim();
				community.style=temp.substring(index6+5, index7).trim();
				community.developer=temp.substring(index7+8, index8).trim();
				community.Property=temp.substring(index8+5).trim();
//				System.out.println("+++"+community.erhouseNum);
//				System.out.println("+++"+community.houseNum);
//				System.out.println("+++"+community.communityLocation);
//				System.out.println("+++"+community.detailLocation);
//				System.out.println("+++"+community.finishTime);
//				System.out.println("+++"+community.style);
//				System.out.println("+++"+community.developer);
//				System.out.println("+++"+community.Property);
//				community.communityLocation=doc.getElementsByAttributeValue("class", "basic-info-ul").text().trim();
			}

		}
		else {
			System.out.print("未抓到页面"+fetchedPage.getStatusCode());
		}
		
		//输出测试
		System.out.println("小区："+community.communityName);

		// 将URL放入已爬取队列
		StaticVisitedUrlQueue.addElement(fetchedPage.getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return community;
	}	
	public static void main(String[] args){
		PageFetcher fetcher=new PageFetcher();
		FetchedPage page=fetcher.getContentFromUrl("http://su.ganji.com/xiaoqu/shimaodongyihao01/");
		GanJiComParser parser=new GanJiComParser();
		parser.parse(page);
	}
}
