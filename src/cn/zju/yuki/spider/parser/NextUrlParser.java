package cn.zju.yuki.spider.parser;
//未用到
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticUrlQueue;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;
import cn.zju.yuki.spider.queue.UrlQueue;
import cn.zju.yuki.spider.queue.VisitedUrlQueue;
import file.MyTime;
import file.Myfile;
//一级页面URL
public class NextUrlParser {
	
	private UrlQueue urlQueue=new UrlQueue();
	private UrlQueue visitedUrlQueue=new UrlQueue();
	private String baseUrl;
	private Elements links;
	
	public NextUrlParser(){
		
	}
	public NextUrlParser(String url){
		this.urlQueue.addElement(url);
		this.getBaseUrl(url);
	}
	public void getBaseUrl(String url){
		int index=url.lastIndexOf("/");
		this.baseUrl=url.substring(0, index);
	}
	/**
	 * 得到所有浏览过的一级链接
	 * @method
	 * @return visitedUrlQueue
	 */
	public UrlQueue parseNextUrl(){
		while(!this.urlQueue.isEmpty()){
			String url=this.urlQueue.outElement();
			PageFetcher fetcher=new PageFetcher();
			FetchedPage fetchedPage=fetcher.getContentFromUrl(url);
			this.parse(fetchedPage);
		}
		return this.visitedUrlQueue;
		
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
				System.out.println("nextURL:"+urlString);
				this.urlQueue.addElement(urlString);
			}
		}
		
		// 将URL放入已爬取队列
//		StaticVisitedUrlQueue.addElement(fetchedPage.getUrl());
//		System.out.println();
		this.visitedUrlQueue.addElement(fetchedPage.getUrl());
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return true;
	}
	
	private boolean containsTargetData(String url, Document contentDoc){
		// 通过URL判断
		// TODO
		if(contentDoc.getElementsByAttributeValue("class", "next") != null){
			this.links=contentDoc.getElementsByAttributeValue("class", "next");
			return true;
		}
		return false;
	}
	
	public static void main(String[] args){
//		PageFetcher fetcher=new PageFetcher();
//		FetchedPage fetchedPage=fetcher.getContentFromUrl("http://su.ganji.com/fang1/o31/");
		long start=System.currentTimeMillis();
		NextUrlParser parser=new NextUrlParser("http://su.ganji.com/fang1");
		UrlQueue queue=new UrlQueue();
//		parser.parse(fetchedPage);
		queue.setUrlQueue(parser.parseNextUrl());
		System.out.println(queue.getUrlQueue());
		long end=System.currentTimeMillis();  
 		long time=end-start;
 		MyTime.gettime(time);
 		System.out.println(time);
	}
	//待处理
//	Myfile mf=new Myfile();
//	if(!mf.isHasFile("GanJi")){
//		System.out.println("文件不存在");
//		String url="http://su.ganji.com/fang1";
//		NextUrlParser nextUrlParser=new NextUrlParser(url);
//		urlQueueLevelOne.setUrlQueue(nextUrlParser.parseNextUrl());
//		System.out.println("urlQueueLevelOne:"+urlQueueLevelOne.getUrlQueue());
//	}
//	else {
//		System.out.println("文件存在");
//	}
}
