package cn.zju.yuki.spider.parser;

import house.AnJuKe;
import house.GanJi;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;

public class AnJuKeParser {
//	private AnJuKe anJuKe=new AnJuKe();
	
	public AnJuKe parse(FetchedPage fetchedPage){
		AnJuKe anJuKe=new AnJuKe();
		Document doc = Jsoup.parse(fetchedPage.getContent());
		anJuKe.url=fetchedPage.getUrl();
		anJuKe.crawlTime=anJuKe.getDate();

		if(fetchedPage.getStatusCode()==200){
			//标题
			if(!doc.getElementsByAttributeValue("class", "tit cf").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				anJuKe.title=doc.getElementsByAttributeValue("class", "tit cf").text();
			}
			else {
				anJuKe.title="";
			}
			
			//发布时间\房源编号
			if(!doc.getElementsByAttributeValue("class", "text-mute extra-info").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "text-mute extra-info").text().trim();

				int index1=temp.indexOf("房源编号：");
				int index2=temp.indexOf("发布时间：");

				if(index1>=0){
					anJuKe.houseNum=temp.substring(index1+5, index2-1);
				}
				if(index2>0){
					anJuKe.time=temp.substring(index2+5);
				}
				
			}
			else {
				anJuKe.time="";
				anJuKe.houseNum="";
			}
			
			//租金、房屋概况	
			if(!doc.getElementsByAttributeValue("class", "p_phrase cf").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				ArrayList<String> temp=new ArrayList<String>();
				for(int i=0;i<11;i++){
					temp.add("");
				}
				for(Element link:doc.getElementsByAttributeValue("class", "p_phrase cf")){
					String str=link.text();
					if(str.contains("租价")&&"".equals(temp.get(0))){
						temp.set(0, str.substring(2));
					}
					if(str.contains("租金押付")&&"".equals(temp.get(1))){
						temp.set(1, str.substring(4));
					}
					if(str.contains("房型")&&"".equals(temp.get(2))){
						temp.set(2, str.substring(2));
					}
					if(str.contains("租赁方式")&&"".equals(temp.get(3))){
						temp.set(3, str.substring(4));
					}
					if(str.contains("类型")&&"".equals(temp.get(4))){
						temp.set(4, str.substring(2));
					}
					if(str.contains("装修")&&"".equals(temp.get(5))){
						temp.set(5, str.substring(2));
					}
					if(str.contains("面积")&&"".equals(temp.get(6))){
						temp.set(6, str.substring(2));
					}
					if(str.contains("朝向")&&"".equals(temp.get(7))){
						temp.set(7, str.substring(2));
					}
					if(str.contains("楼层")&&"".equals(temp.get(8))){
						temp.set(8, str.substring(2));
					}
					if(str.contains("小区名")&&"".equals(temp.get(9))){
						temp.set(9, str.substring(3));
					}
					if(str.contains("所在版块")&&"".equals(temp.get(10))){
						temp.set(10, str.substring(4));
					}
				}
				anJuKe.pay=temp.get(0).trim();
				anJuKe.payStyle=temp.get(1).trim();
				anJuKe.houseStyle=temp.get(2).trim();
				anJuKe.rentStyle=temp.get(3).trim();
				anJuKe.style=temp.get(4).trim();
				anJuKe.houseResume=temp.get(5).trim();
				anJuKe.area=temp.get(6).trim();
				anJuKe.direction=temp.get(7).trim();
				anJuKe.floor=temp.get(8).trim();
				anJuKe.community=temp.get(9).trim();
				anJuKe.location="苏州"+temp.get(10).replace(" ", "-");
			}
			
			//peizhi
			if(!doc.getElementsByAttributeValue("class", "pro_links").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp="";
				for(Element link:doc.getElementsByAttributeValue("class", "pro_links")){
					temp+=link.text();
				}
				int index=temp.indexOf("：");
				anJuKe.installation=temp.substring(index+1);
			}
			else {
				anJuKe.installation="";

			}
			//联系人
			if(!doc.getElementsByAttributeValue("class", "dark_grey f16 t_c").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				anJuKe.contactMan=doc.getElementsByAttributeValue("class", "dark_grey f16 t_c").text().trim();
			}
			else {
				anJuKe.contactMan="";
			}
			
			//联系方式
			if(!doc.getElementsByAttributeValue("class", "broker_icon broker_tel dark_grey").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				anJuKe.contactWay=doc.getElementsByAttributeValue("class", "broker_icon broker_tel dark_grey").text().trim();
			}
			else {
				anJuKe.contactWay="";
			}
			//公司 店面
			if(!doc.getElementsByAttributeValue("class", "ibk").isEmpty()){
				// 解析并获取目标数据
				// TODO
				ArrayList<String > temp=new ArrayList<String>();
				for(int i=0;i<2;i++){
					temp.add("");
				}
				for(Element element:doc.getElementsByAttributeValue("class", "ibk")){
					String str=element.text();
					if(str.contains("公司：")&&"".equals(temp.get(0))){
						temp.set(0, str.substring(3));
					}
					if(str.contains("门店：")&&"".equals(temp.get(1))){
						temp.set(1, str.substring(3));
					}
				}
				anJuKe.company=temp.get(0);
				anJuKe.subCompany=temp.get(1);
			}
			else {
				anJuKe.company="";
				anJuKe.subCompany="";
			}
			//房源描述
			if(!doc.getElementsByAttributeValue("class", "pro_main cf").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "pro_main cf").text().trim();
				anJuKe.houseDetails=temp.replaceAll(" +", "");
			}
			else {
				anJuKe.houseDetails="";

			}
			if(!doc.getElementsByAttributeValue("class", "tabs cf").isEmpty()){
				// 解析并获取目标数据
				// TODO
				String temp=doc.getElementsByAttributeValue("class", "tabs cf").text().trim();
				int in=temp.indexOf("室内图（");
				int out=temp.indexOf("室外图（");
				if(in>=0){
					anJuKe.picIn=temp.substring(in+4,out).replace("）", "");
				}
				if(out>0){
					anJuKe.picOut=temp.substring(out+4).replace("）", "");
				}
			}
		}
		else {
			System.out.print(fetchedPage.getStatusCode());
		}
		
		//输出测试
//		System.out.println("来源："+anJuKe.source);
//		System.out.println("URL："+anJuKe.url);
//		System.out.println("houseNum:"+anJuKe.houseNum);
//		System.out.println("title："+anJuKe.title);
//		System.out.println("time:"+anJuKe.time);
//		System.out.println("pay:"+anJuKe.pay);
//		System.out.println("payStyle:"+anJuKe.payStyle);
//		System.out.println("rentStyle:"+anJuKe.rentStyle);
//		System.out.println("style:"+anJuKe.style);
//		System.out.println("area:"+anJuKe.area);
//		System.out.println("direction:"+anJuKe.direction);
//		System.out.println("houseStyle:"+anJuKe.houseStyle);
//		System.out.println("houseResume:"+anJuKe.houseResume);
//		System.out.println("floor:"+anJuKe.floor);
//		System.out.println("community:"+anJuKe.community);
//		System.out.println("location:"+anJuKe.location);
//		System.out.println(anJuKe.installation);
//		System.out.println(anJuKe.contactMan);
//		System.out.println(anJuKe.contactWay);
//		System.out.println("company:"+anJuKe.company);
//		System.out.println("subCompany:"+anJuKe.subCompany);
//		System.out.println(anJuKe.houseDetails);
//		System.out.println(anJuKe.crawlTime);
//		System.out.println("picIn:"+anJuKe.picIn);
//		System.out.println("picOut:"+anJuKe.picOut);
//		
		// 将URL放入已爬取队列
		StaticVisitedUrlQueue.addElement("未抓到页面"+fetchedPage.getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return anJuKe;
	}
	
	public static void main(String[] args){
		PageFetcher fetcher=new PageFetcher();
		FetchedPage page=fetcher.getContentFromUrl("http://su.zu.anjuke.com/fangyuan/39197857");
		AnJuKeParser parser=new AnJuKeParser();
		parser.parse(page);
	}
}
