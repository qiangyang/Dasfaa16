package cn.zju.yuki.spider.parser;

import java.util.ArrayList;

import house.GanJi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;
//赶集网二级页面信息获取
public class GanJiParser {
	
//	private GanJi ganJi=new GanJi();
	
	public GanJi parse(FetchedPage fetchedPage){
		GanJi ganJi=new GanJi();
		Document doc = Jsoup.parse(fetchedPage.getContent());
		ganJi.url=fetchedPage.getUrl();
		ganJi.crawlTime=ganJi.getDate();

		if(fetchedPage.getStatusCode()==200){
			//标题
			if(!doc.getElementsByTag("h1").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				ganJi.title=doc.getElementsByTag("h1").text();
			}
			else {
				ganJi.title="";
			}
			
			//发布时间
			if(!doc.getElementsByAttributeValue("class", "f10 pr-5").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				ganJi.time=doc.getElementsByAttributeValue("class", "f10 pr-5").text().trim();
			}
			else {
				ganJi.time="";
			}
			
			//租金、房屋概况	
			if(!doc.select("li[style]").isEmpty()){
				// 解析并获取目标数据
				// TODO	
//				String temp="";
				ArrayList<String> temp=new ArrayList<String>();
				for(int i=0;i<6;i++){
					temp.add("");
				}
				for(Element link:doc.select("li[style]")){
					String str=link.text();
					if(str.contains("租金")&&"".equals(temp.get(0))){
						temp.set(0, str);
					}
					if(str.contains("户型")&&"".equals(temp.get(1))){
						temp.set(1, str);
					}
					if(str.contains("概况")&&"".equals(temp.get(2))){
						temp.set(2, str);
					}
					if(str.contains("楼层")&&"".equals(temp.get(3))){
						temp.set(3, str);
					}
					if(str.contains("小区")&&"".equals(temp.get(4))){
						temp.set(4, str);
					}
					if(str.contains("位置")&&"".equals(temp.get(5))){
						temp.set(5, str);
					}
					
				}
				int i;
				i=temp.get(0).indexOf("：");
				if(i>0){
					String payString=temp.get(0).substring(i+1).trim();
					int payIndex=payString.indexOf("/");
					if(payIndex>0){
						ganJi.pay=payString.substring(0, payIndex+2).trim();
					}
					payIndex=payString.indexOf("押");
					if(payIndex>0){
						ganJi.payStyle=payString.substring(payIndex).replace(")", "").trim();
					}
				}
				else {
					ganJi.pay="";
				}
				i=temp.get(1).indexOf("：");
				if(i>0){
					String houseString=temp.get(1).substring(i+1).trim();
					int index=houseString.indexOf("卫");
					if(index>0){
						ganJi.houseStyle=houseString.substring(0, index);
						String[] array=houseString.split("-");
						ganJi.rentStyle=array[1].trim();
						ganJi.area=array[2].replace(" ", "").trim();
					}
					else {
						String[] array=houseString.split("-");
						ganJi.rentStyle=array[0].trim();
						ganJi.area=array[1].replace(" ", "").trim();
					}
				}
				else {
					ganJi.houseStyle="";
				}
				i=temp.get(2).indexOf("：");
				if(i>0){
					String houseResume=temp.get(2).substring(i+1).trim();
					if(houseResume.indexOf("-")>0){
						String[] array=houseResume.split("-");
						for(String str:array){
							if(str.contains("朝")){
								ganJi.direction=str.trim();
							}
							else if(str.contains("修")||str.contains("毛坯")){
								ganJi.decoration=str.trim();
							}
							else {
								ganJi.style=str.trim();
							}
						}
					}
				}
				else {
					ganJi.decoration="";
				}
				i=temp.get(3).indexOf("：");
				if(i>0){
					ganJi.floor=temp.get(3).substring(i+1).trim();
				}
				else {
					ganJi.floor="";
				}
				i=temp.get(4).indexOf("：");
				if(i>0){
					String[] array=temp.get(4).substring(i+1).trim().split("-");
					String communityStr=array[0].toString().replaceAll("   ", "");
					int index=communityStr.indexOf("(");
					if(index>0){
						communityStr=communityStr.substring(0,index).trim();
					}
					ganJi.community=communityStr.trim();
				}
				else {
					ganJi.community="";
				}
				i=temp.get(5).indexOf("：");
				if(i>0){
					ganJi.location=temp.get(5).substring(i+1).trim().replace(" ", "");
				}
				else {
					ganJi.location="";
				}
			}
			else {
				ganJi.pay="";
				ganJi.houseStyle="";
				ganJi.decoration="";
				ganJi.floor="";
				ganJi.community="";
//				ganJi.location="";

			}
			
			//peizhi
			if(!doc.select("li.peizhi").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp="";
				for(Element link:doc.select("li.peizhi")){
					temp+=link.text();
				}
				int index=temp.indexOf("：");
				ganJi.installation=temp.substring(index+1).replace(" ", "").trim();

				
			}
			else {
				ganJi.installation="";

			}
			//联系人、方式
			if(!doc.getElementsByAttributeValue("class", "talk-btn").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				ganJi.contactMan=doc.getElementsByAttributeValue("class", "talk-btn").attr("data-username").toString();
				ganJi.contactWay=doc.getElementsByAttributeValue("class", "talk-btn").attr("data-phone").toString();
			}
			else {
				ganJi.contactMan="";
				ganJi.contactWay="";

			}
			//房源描述
			if(!doc.select("div.summary-cont").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp="";
				for(Element link:doc.select("div.summary-cont")){
					temp+=link.text().toString().trim();
				}
				ganJi.houseDetails=temp.replaceAll(" +", "").replaceAll(" +", "").replaceAll("联系我时，请说是在赶集网上看到的，谢谢！", "");

				
			}
			else {
				ganJi.houseDetails="";

			}
			//图片数
			if(!doc.getElementsByAttributeValue("class", "cont-box pics").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "cont-box pics").toString();
				String temp2=temp.replaceAll("img", "");
				ganJi.picNum=(temp.length()-temp2.length())/3;
			}
			else {
				ganJi.picNum=0;
			}
			
			//location
//			if(!doc.getElementsByAttributeValue("class", "map-top").isEmpty()){
//				// 解析并获取目标数据
//				// TODO	
//				String temp=doc.getElementsByAttributeValue("class", "map-top").text().trim();
//				ganJi.location=temp.replaceAll(" ", "");
//			}
			
			//pageviews
//			if(!doc.getElementsByAttributeValue("id", "pageviews").isEmpty()){
//				// 解析并获取目标数据
//				// TODO	
//				String temp=doc.getElementsByAttributeValue("id", "pageviews").text().trim();
//				ganJi.pageviews=Integer.parseInt(temp);
//			}
		}
		else {
			System.out.print("未抓到页面"+fetchedPage.getStatusCode());
		}
		
		//输出测试
//		System.out.println("来源："+ganJi.source);
//		System.out.println("URL："+ganJi.url);
//		System.out.println("title："+ganJi.title);
//		System.out.println("time:"+ganJi.time);
//		
//		System.out.println("pay:"+ganJi.pay);
//		System.out.println("payStyle:"+ganJi.payStyle);
//		System.out.println("houseStyle:"+ganJi.houseStyle);
//		System.out.println("rentStyle:"+ganJi.rentStyle);
//		
//		System.out.println("decoration:"+ganJi.decoration);
//		System.out.println("style:"+ganJi.style);
//		System.out.println("area:"+ganJi.area);
//		System.out.println("direction:"+ganJi.direction);
//		
//		System.out.println("floor:"+ganJi.floor);
//		System.out.println("community:"+ganJi.community);
//		System.out.println("location:"+ganJi.location);
//		System.out.println(ganJi.installation);
//		System.out.println(ganJi.contactMan);
//		System.out.println(ganJi.contactWay);
//		System.out.println(ganJi.houseDetails);
//		System.out.println(ganJi.crawlTime);
//		System.out.println("picNum:"+ganJi.picNum);
//		System.out.println("pageviews:"+ganJi.pageviews);
		// 将URL放入已爬取队列
		StaticVisitedUrlQueue.addElement(fetchedPage.getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return ganJi;
	}	
	public static void main(String[] args){
		PageFetcher fetcher=new PageFetcher();
		FetchedPage page=fetcher.getContentFromUrl("http://su.ganji.com/fang1/1402701288x.htm");
		GanJiParser parser=new GanJiParser();
		parser.parse(page);
	}
}
