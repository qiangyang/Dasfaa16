package cn.zju.yuki.spider.parser;

import javax.lang.model.element.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;

public class ContentParser {
	public Object parse(FetchedPage fetchedPage){
		Object targetObject = null;
		Document doc = Jsoup.parse(fetchedPage.getContent());
		
		
		// 如果当前页面包含目标数据
		if(containsTargetData(fetchedPage.getUrl(), doc)){
			// 解析并获取目标数据
			// TODO			
		}
		
		// 将URL放入已爬取队列
		StaticVisitedUrlQueue.addElement(fetchedPage.getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return targetObject; 
	}
	
	private boolean containsTargetData(String url, Document contentDoc){
		// 通过URL判断
		// TODO
		
		// 通过content判断，比如需要抓取class为grid_view中的内容
//		if(contentDoc.getElementsByClass("list-mod4") != null){
//			System.out.println("www"+contentDoc.getElementsByClass("list-mod4").toString());
//			return true;
//		}
//		if(contentDoc.getElementsByTag("a") != null){
//			System.out.println("<a>元素："+contentDoc.getElementsByTag("a").toString());
//			return true;
//		}
//		if(contentDoc.getElementsByAttributeValue("class", "list-info-title js-title") != null){
//			this.links=contentDoc.getElementsByAttributeValue("class", "list-info-title js-title");
//			return true;
//		}
		
		return false;
	}
	
//	public static void main(String[] args){
//		PageFetcher fetcher=new PageFetcher();
//		FetchedPage page=fetcher.getContentFromUrl("http://su.ganji.com/fang1/");
//		ContentParser parser=new ContentParser();
//		parser.parse(page);
//	}
}
