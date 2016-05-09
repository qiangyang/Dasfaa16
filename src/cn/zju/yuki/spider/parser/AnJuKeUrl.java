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

public class AnJuKeUrl {
	private UrlQueue urlQueue=new UrlQueue();
	private Elements links;
	
	public AnJuKeUrl(){
		
	}
	
	public AnJuKeUrl(UrlQueue queue){
		this.urlQueue.setUrlQueue(queue);
	}
	
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
				String urlString=link.attr("link").toString();
//				if(!urlString.contains("http")){
//					urlString=urlString;
//				}
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
		if(contentDoc.getElementsByAttributeValue("class", "dl_list_house clear_box") != null){
			this.links=contentDoc.getElementsByAttributeValue("class", "dl_list_house clear_box");
			return true;
		}
		return false;
	}
	
	public static void main(String[] args){
//		PageFetcher fetcher=new PageFetcher();
//		FetchedPage page=fetcher.getContentFromUrl("http://su.ganji.com/fang1");
		UrlQueue queue=new UrlQueue();
		queue.addElement("http://su.zu.anjuke.com/fangyuan/p1-px3/#filtersort");
		AnJuKeUrl parser=new AnJuKeUrl(queue);
		parser.getUrlByParse();;
		System.out.println(StaticUrlQueue.getUrlQueue());
	}
}
