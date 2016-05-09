package cn.zju.yuki.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticUrlQueue;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;
import cn.zju.yuki.spider.queue.UrlQueue;

public class GanJiUrl extends ContentParser{
	private UrlQueue urlQueue=new UrlQueue();
	private Elements links;
	private String baseUrl="http://su.ganji.com";
	
	public GanJiUrl(){
		
	}
	
	public GanJiUrl(UrlQueue queue){
		this.urlQueue.setUrlQueue(queue);
	}
	
//	public void getBaseUrl(){
//		int index=
//	}
	public void getUrlByParse(){
		while(!this.urlQueue.isEmpty()){
			String url=this.urlQueue.outElement();
			PageFetcher fetcher=new PageFetcher();
			FetchedPage fetchedPage=fetcher.getContentFromUrl(url);
			this.parse(fetchedPage);
		}
	}
	
	public Object parse(FetchedPage fetchedPage){
		Document doc = Jsoup.parse(fetchedPage.getContent());
		// 如果当前页面包含目标数据
		if(containsTargetData(fetchedPage.getUrl(), doc)){
			// 解析并获取目标数据
			// TODO	
			for(Element link:this.links){
				String urlString=link.attr("href").toString();
				if(!urlString.contains("http")){
					urlString=this.baseUrl+urlString;
				}
//				System.out.println("URL:"+urlString);
				StaticUrlQueue.addElement(urlString);
			}
		}
		
		// 将URL放入已爬取队列
		StaticVisitedUrlQueue.addElement(fetchedPage.getUrl());
//		System.out.println();
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return true;
	}
	
	private boolean containsTargetData(String url, Document contentDoc){
		// 通过URL判断
		// TODO
		if(contentDoc.getElementsByAttributeValue("class", "list-info-title js-title") != null){
			this.links=contentDoc.getElementsByAttributeValue("class", "list-info-title js-title");
			return true;
		}
		
		return false;
	}
	
	
	public static void main(String[] args){
//		PageFetcher fetcher=new PageFetcher();
//		FetchedPage page=fetcher.getContentFromUrl("http://su.ganji.com/fang1");
		UrlQueue queue=new UrlQueue();
		queue.addElement("http://su.ganji.com/fang1/");
		GanJiUrl parser=new GanJiUrl(queue);
		parser.getUrlByParse();;
		System.out.println(StaticUrlQueue.getUrlQueue());
	}
}
