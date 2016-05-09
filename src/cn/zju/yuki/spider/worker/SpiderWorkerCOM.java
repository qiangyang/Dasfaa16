package cn.zju.yuki.spider.worker;

import house.AnJuKe;
import house.Community;
import house.FiveEight;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.handler.ContentHandler;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.model.SpiderParams;
import cn.zju.yuki.spider.parser.AnJuKeParser;
import cn.zju.yuki.spider.parser.GanJiComParser;
import cn.zju.yuki.spider.queue.StaticUrlQueue;
import cn.zju.yuki.spider.storage.DataStorage;

public class SpiderWorkerCOM implements Runnable{
	private static final Logger Log = Logger.getLogger(SpiderWorker.class.getName());
	private static ArrayList<FiveEight> fiveEights=new ArrayList<FiveEight>();
	
	private PageFetcher fetcher;
	private ContentHandler handler;
	private GanJiComParser info;
	private DataStorage store;
	private int threadIndex;
	
	public SpiderWorkerCOM(int threadIndex){
		this.threadIndex = threadIndex;
		this.fetcher = new PageFetcher();
		this.handler = new ContentHandler();
		this.info=new GanJiComParser();
		this.store = new DataStorage();
//		StaticUrlQueue.addElement("http://su.ganji.com/fang1/1351782251x.htm");
	}
	
	
	@Override
	public void run() {
		// 登录
		
		
		// 当待抓取URL队列不为空时，执行爬取任务
		// 注： 当队列内容为空时，也不爬取任务已经结束了
		//     因为有可能是UrlQueue暂时空，其他worker线程还没有将新的URL放入队列
		//	        所以，这里可以做个等待时间，再进行抓取（二次机会）
		while(!StaticUrlQueue.isEmpty()){
			// 从待抓取队列中拿URL
			String url = StaticUrlQueue.outElement();
			
			// 抓取URL指定的页面，并返回状态码和页面内容构成的FetchedPage对象
			FetchedPage fetchedPage = fetcher.getContentFromUrl(url);
//			System.out.println(fetchedPage.getContent());
			// 检查爬取页面的合法性，爬虫是否被禁止
//			if(!handler.check(fetchedPage)){
//				// 切换IP等操作
//				// TODO
//				
//				Log.info("Spider-" + threadIndex + ": switch IP to ");
//				System.out.println("请输入验证码");
//				continue;
//			}
			
			// 解析页面，获取目标数据
			try {
				Community targetData = info.parse(fetchedPage);
				if("".equals(targetData.communityName)||null==targetData.communityName){
					System.out.println(fetchedPage.getUrl());
					System.out.println("爬虫被禁...请去输入验证码(Y/N):");
					
					String str="N";
					Scanner input=new Scanner(System.in);
					str=input.next();
					if("Y".equals(str)){
						System.out.println("验证完毕..开始爬虫..");
						StaticUrlQueue.addFirstElement(fetchedPage.getUrl());
						continue;
					}
					else {
						System.out.println("验证失败");
						StaticUrlQueue.addFirstElement(fetchedPage.getUrl());
						continue;
					}
				}
				else {
//					targetData.show();
					store.store(targetData);
				}
//				if((fiveEights.size()==500)||(fiveEights.size()<500&&StaticUrlQueue.isEmpty())){
//					ArrayList<FiveEight> deleteList=new ArrayList<FiveEight>();
//					for(FiveEight element:fiveEights){
//						System.out.println(element.title);
//						// 存储目标数据到数据存储（如DB）、存储已爬取的Url到VisitedUrlQueue
//						store.store(targetData);
//						deleteList.add(element);
//					}
//					fiveEights.removeAll(deleteList);
//				}
//				else {
//					fiveEights.add(targetData);
//				}
				
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
				continue;
			}
			
			
			
			// delay
			try {
				Thread.sleep(SpiderParams.DEYLAY_TIME);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		fetcher.close();
		Log.info("Spider-" + threadIndex + ": stop...");
	}
}
