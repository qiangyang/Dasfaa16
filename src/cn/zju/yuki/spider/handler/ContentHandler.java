package cn.zju.yuki.spider.handler;

import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.StaticUrlQueue;

public class ContentHandler {
	public boolean check(FetchedPage fetchedPage){
		// 如果抓取的页面包含反爬取内容，则将当前URL放入待爬取队列，以便重新爬取
		if(isAntiScratch(fetchedPage)){
			StaticUrlQueue.addFirstElement(fetchedPage.getUrl());
			return false;
		}
		
		return true;
	}
	
	private boolean isStatusValid(int statusCode){
		if(statusCode >= 200 && statusCode < 400){
			return true;
		}
		return false;
	}
	
	private boolean isAntiScratch(FetchedPage fetchedPage){
		// 403 forbidden
		if((!isStatusValid(fetchedPage.getStatusCode())) && fetchedPage.getStatusCode() == 403){
			return true;
		}
		
		// 页面内容包含的反爬取内容
		if(fetchedPage.getContent().contains("<div class='search_tips_info'>对不起...你的访问过于频繁，请输入验证码继续访问：</div>")){
			return true;
		}
		
		return false;
	}
}
