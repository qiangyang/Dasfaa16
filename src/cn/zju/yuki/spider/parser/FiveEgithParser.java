package cn.zju.yuki.spider.parser;

import java.util.ArrayList;

import house.FiveEight;
import house.GanJi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;

public class FiveEgithParser {
	
//	private FiveEight fiveEight=new FiveEight();
	
	public FiveEight parse(FetchedPage fetchedPage){
		FiveEight fiveEight=new FiveEight();
		Document doc = Jsoup.parse(fetchedPage.getContent());
		fiveEight.url=fetchedPage.getUrl();
		fiveEight.crawlTime=FiveEight.getDate();

		if(fetchedPage.getStatusCode()==200){
			//标题
			if(!doc.getElementsByTag("h1").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				fiveEight.title=doc.getElementsByTag("h1").text();
			}
			else {
				fiveEight.title="";
			}
			
//			//发布时间(58时间不好抓)
			if(!doc.getElementsByAttributeValue("class", "time").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "time").text().trim();
				if("".equals(temp)||null==temp){
					fiveEight.time=fiveEight.getDate();
				}
				else {
					fiveEight.time=temp;
				}
			}
			else {
				fiveEight.time="";
			}
			
			//租金、房屋概况	楼层
			if(!doc.getElementsByAttributeValue("class", "su_con").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				ArrayList<String> temp=new ArrayList<String>();
				for(Element element:doc.getElementsByAttributeValue("class", "su_con")){
					temp.add(element.text().trim());
				}
				System.out.println(temp);
				String payString=temp.get(0).replaceAll(" +", "");
//				System.out.println(payString);
				int index=payString.indexOf("押");
				if(index>0){
					fiveEight.pay=payString.substring(0, index).replaceAll(" ", "");
					fiveEight.payStyle=payString.substring(index).replaceAll(" ", "");
				}
				else {
					fiveEight.pay=payString;
					fiveEight.payStyle="";
				}
//				System.out.println(temp.get(1));
				int i=1;
				while(i<temp.size()-2){//&&(null!=temp.get(i)||!"".equals(temp.get(i)))
					if(temp.get(i).contains("层")){
						fiveEight.floor=temp.get(2).replaceAll(" +", "").replaceAll(" ", "");
					}
					else {
						String[] array=temp.get(i).split("   ");
//						for(int k=0;k<array.length;k++){
//							System.out.println(array[k]);
//						}
						for(String str:array){
							this.deal(str,fiveEight);
						}
					}
					i++;
				}
//				String[] houseResume=temp.get(1).split("    ");
//				for(String str:houseResume){
//					this.deal(str);
//				}
//				if(temp.size()>2&&!"".equals(temp.get(2))){
//					if(temp.get(2).contains("层")){
//						fiveEight.floor=temp.get(2).replaceAll(" +", "").replaceAll(" ", "");
//					}
//					else {
//						String[] array=temp.get(2).split("    ");
//						for(String str:array){
//							this.deal(str);
//						}
//					}
//					
//				}
//				if(temp.size()>3&&!"".equals(temp.get(3))){
//					String[] array=temp.get(3).split("    ");
//					for(String str:array){
//						this.deal(str);
//					}
//				}
//				else {
//					fiveEight.floor="";
//				}
			}
			else {
				fiveEight.pay="";
				fiveEight.floor="";

			}
			//区域  位置
			if(!doc.getElementsByAttributeValue("class", "su_con w382").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				ArrayList<String> temp=new ArrayList<String>();
				for(Element element:doc.getElementsByAttributeValue("class", "su_con w382")){
					temp.add(element.text().replaceAll(" +", "").trim());
				}
				System.out.println(temp);
				int index=temp.get(0).lastIndexOf("-");
				if(index>0){
					fiveEight.community=temp.get(0).substring(index+1).replaceAll(" ", "");
					fiveEight.location=temp.get(0).substring(0, index).replaceAll(" ", "");
				}
				else {
					fiveEight.community="";
					fiveEight.location="";
				}
				if(temp.size()==2){
					fiveEight.detailLocation=temp.get(1).trim();
				}
				//对小区字段进行处理
				if(!"".equals(fiveEight)&&null!=fiveEight){
					if(fiveEight.community.contains("（")){
						int i=fiveEight.community.indexOf("（");
						fiveEight.community=fiveEight.community.substring(0, i).replaceAll(" ", "");
					}
					else if(fiveEight.community.contains("找")){
						int i=fiveEight.community.indexOf("找");
						fiveEight.community=fiveEight.community.substring(0, i).replaceAll(" ", "");
					}
				}
				
			}
			else {
				fiveEight.community="";
				fiveEight.location="";
			}
			//联系人
			if(!doc.getElementsByAttributeValue("style", "float:left").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				fiveEight.contactMan=doc.getElementsByAttributeValue("style", "float:left").text().trim();
				
			}
			else {
				fiveEight.contactMan="";

			}
			
			//联系方式
			if(!doc.getElementsByAttributeValue("class", "su_phone").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "su_phone").text().replaceAll(" +", "");
//				System.out.println(temp);
				int i=temp.indexOf("电话归属地");
//				System.out.println(i);
				if(temp.length()>=11&&i>=12){
					fiveEight.contactWay=temp.substring(0,11);
				}
				else{
					fiveEight.contactWay="";
				}
			}
			else {
				fiveEight.contactWay="";

			}
			
			//peizhi
			if(!doc.getElementsByTag("script").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				for(Element element:doc.getElementsByTag("script")){
					if(element.data().startsWith("var tmp")){
						String temp=element.data().replaceAll(" +", "");
						int quotation=temp.indexOf("'");//引号
						int semicolon=temp.indexOf(";");//分号
						fiveEight.installation=temp.substring(quotation+1, semicolon-1);
						break;
					}
				}
			}
			else {
				fiveEight.installation="";

			}
			
			//房源描述
			if(!doc.getElementsByAttributeValue("class", "col_sub description").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp="";
				for(Element link:doc.getElementsByAttributeValue("class", "col_sub description")){
					temp+=link.text().toString().trim();
				}
				int index=temp.indexOf("联系我时，请说是在58同城上看到的，谢谢！");
				fiveEight.houseDetails=temp.substring(0, index).replaceAll(" ", "");
			}
			else {
				fiveEight.houseDetails="";

			}
			
			//图片数
			if(!doc.getElementsByAttributeValue("class", "descriptionImg").isEmpty()){
				// 解析并获取目标数据
				// TODO	
				String temp=doc.getElementsByAttributeValue("class", "descriptionImg").toString();
				String temp2=temp.replaceAll("img", "");
				fiveEight.picNum=(temp.length()-temp2.length())/3;
			}
			else {
				fiveEight.picNum=0;
			}
		}
		else {
			System.out.print("未抓到页面"+fetchedPage.getStatusCode());
		}
		
		//输出测试
//		System.out.println("来源："+fiveEight.source);
//		System.out.println("URL："+fiveEight.url);
//		System.out.println("title："+fiveEight.title);
//		System.out.println("time:"+fiveEight.time);
//		System.out.println("pay:"+fiveEight.pay);
//		System.out.println("payStyle:"+fiveEight.payStyle);
//		System.out.println("houseStyle:"+fiveEight.houseStyle);
//		System.out.println("rentStyle:"+fiveEight.rentStyle);
//		
//		System.out.println("decoration:"+fiveEight.decoration);
//		System.out.println("style:"+fiveEight.style);
//		System.out.println("area:"+fiveEight.area);
//		System.out.println("direction:"+fiveEight.direction);
//	
//		System.out.println("floor:"+fiveEight.floor);
//		System.out.println("community:"+fiveEight.community);
//		System.out.println("location:"+fiveEight.location);
//		System.out.println("detailLocation:"+fiveEight.detailLocation);
//		System.out.println("installation:"+fiveEight.installation);
//		System.out.println("contactMan:"+fiveEight.contactMan);
//		System.out.println("contactWay:"+fiveEight.contactWay);
//		System.out.println("houseDetails:"+fiveEight.houseDetails);
//		System.out.println("crawlTime:"+fiveEight.crawlTime);
//		System.out.println("picNum:"+fiveEight.picNum);
//		// 将URL放入已爬取队列
		StaticVisitedUrlQueue.addElement(fetchedPage.getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return fiveEight;
	}
	
	public void deal(String str,FiveEight fiveEight){
		if((str.contains("卫")||str.contains("厅")||str.contains("室"))&&str.contains("㎡")){
			if(str.indexOf("卫")>0){
				int i=str.indexOf("卫");
				fiveEight.houseStyle=str.substring(0, i+1).replaceAll(" +", "");
				fiveEight.area=str.substring(i-1).replaceAll(" +", "");
			}
			else if(str.indexOf("厅")>0){
				int i=str.indexOf("厅");
				fiveEight.houseStyle=str.substring(0, i+1).replaceAll(" +", "");
				fiveEight.area=str.substring(i-1).replaceAll(" +", "");
			}
			else if(str.indexOf("室")>0){
				int i=str.indexOf("室");
				fiveEight.houseStyle=str.substring(0, i+1).replaceAll(" +", "");
				fiveEight.area=str.substring(i-1).replaceAll(" +", "");
			}
		}
		else if(str.contains("卫")||str.contains("厅")||str.contains("室")){
			fiveEight.houseStyle=str.replaceAll(" +", "").replaceAll(" ", "");
		}
		else if(str.contains("㎡")){
			fiveEight.area=str.replaceAll(" +", "").replaceAll(" ", "");
		}
		else if(str.contains("修")||str.contains("毛坯")){
			fiveEight.decoration=str.replaceAll(" +", "").replaceAll(" ", "");
		}
		else if(str.contains("朝")){
			fiveEight.direction=str.replaceAll(" +", "").replaceAll(" ", "");
		}
		else if(str.contains("租")||str.contains("卧")||str.contains("男")||str.contains("女")){
			fiveEight.rentStyle+=str.replaceAll(" +", "").replaceAll(" ", "");
		}
		else {
			fiveEight.style=str.replaceAll(" +", "").replaceAll(" ", "");
		}
	}
	public static void main(String[] args){
		PageFetcher fetcher=new PageFetcher();
		FetchedPage page=fetcher.getContentFromUrl("http://su.58.com/zufang/21230281947934x.shtml");
		FiveEgithParser parser=new FiveEgithParser();
		parser.parse(page);
	}
}
