package cn.zju.yuki.spider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.model.SpiderParams;
import cn.zju.yuki.spider.parser.AnJuKeUrl;
import cn.zju.yuki.spider.parser.FiveEightUrl;
import cn.zju.yuki.spider.parser.GanJIComURL;
import cn.zju.yuki.spider.parser.NextUrlParser;
import cn.zju.yuki.spider.parser.GanJiUrl;
import cn.zju.yuki.spider.queue.StaticUrlQueue;
import cn.zju.yuki.spider.queue.StaticVisitedUrlQueue;
import cn.zju.yuki.spider.queue.UrlQueue;
import cn.zju.yuki.spider.queue.VisitedUrlQueue;
import cn.zju.yuki.spider.worker.SpiderWorker;
import cn.zju.yuki.spider.worker.SpiderWorker2;
import cn.zju.yuki.spider.worker.SpiderWorker3;
import cn.zju.yuki.spider.worker.SpiderWorkerCOM;
import file.Myfile;


public class SpiderStarter {
    private static UrlQueue urlQueueLevelOne=new UrlQueue();
    private UrlQueue urlQueueLevelTwo=new UrlQueue();
    private VisitedUrlQueue visitedUrlQueueLevelOne=new VisitedUrlQueue();
    private VisitedUrlQueue visitedUrlQueueLevelTwo=new VisitedUrlQueue();
    
	public static void main(String[] args){
		// 初始化配置参数
		initializeParams();

		// 初始化爬取队列   赶集网
		initializeQueueGanJi();
		System.out.println(StaticUrlQueue.getUrlQueue().size());
		System.out.println(StaticVisitedUrlQueue.getVisitedUrlQueue().get(0));//显示最先访问的url
		// 创建worker线程并启动
		System.out.println("开始爬虫(Y/N):");
		String str="N";
		Scanner input=new Scanner(System.in);
		str=input.next();
		if("Y".equals(str)){
			for(int i = 1; i <= SpiderParams.WORKER_NUM; i++){
				new Thread(new SpiderWorker(i)).start();
			}
		}

//		StaticUrlQueue.clearQueue();
		
		// 初始化爬取队列  58同城
		initializeQueueFiveEight();
		System.out.println(StaticUrlQueue.getUrlQueue().size());
		System.out.println(StaticVisitedUrlQueue.getVisitedUrlQueue().get(0));//显示最先访问的url
		System.out.println("开始爬虫(Y/N):");
		String str1="N";
		Scanner input1=new Scanner(System.in);
		str1=input1.next();
		// 创建worker线程并启动Y
		if("Y".equals(str1)){
			for(int i = 1; i <= SpiderParams.WORKER_NUM; i++){
				new Thread(new SpiderWorker2(i)).start();
			}
		}
		
		// 初始化爬取队列  安居客
//		initializeQueueAnJuKe();
//		System.out.println(StaticUrlQueue.getUrlQueue().size());
//		System.out.println(StaticVisitedUrlQueue.getVisitedUrlQueue().get(0));//显示最先访问的url
//		System.out.println("开始爬虫(Y/N):");
//		String str2="N";
//		Scanner input2=new Scanner(System.in);
//		str2=input2.next();
//		// 创建worker线程并启动Y
//		if("Y".equals(str2)){
//			for(int i = 1; i <= SpiderParams.WORKER_NUM; i++){
//				new Thread(new SpiderWorker3(i)).start();
//			}
//		}
		
		// 初始化爬取队列  小区
//		initializeQueueCOM();
//		System.out.println(StaticUrlQueue.getUrlQueue().size());
//		System.out.println(StaticVisitedUrlQueue.getVisitedUrlQueue().get(0));//显示最先访问的url
//		System.out.println("开始爬虫(Y/N):");
//		String str3="N";
//		Scanner input3=new Scanner(System.in);
//		str3=input3.next();
//		// 创建worker线程并启动Y
//		if("Y".equals(str3)){
//			for(int i = 1; i <= SpiderParams.WORKER_NUM; i++){
//				new Thread(new SpiderWorkerCOM(i)).start();
//			}
//		}		
	}
	
	/**
	 * 初始化配置文件参数
	 */
	private static void initializeParams(){
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream("conf/spider.properties"));
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取参数
			SpiderParams.WORKER_NUM = Integer.parseInt(properties.getProperty("spider.threadNum"));
			SpiderParams.DEYLAY_TIME = Integer.parseInt(properties.getProperty("spider.fetchDelay"));

			in.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(SpiderParams.DEYLAY_TIME+SpiderParams.WORKER_NUM+"initializeParams成功");
	}
	
	/**
	 * 准备初始的爬取链接  赶集网
	 */
	private static void initializeQueueGanJi(){
		// 赶集网租房250个一级页面，根据链接规则生成二级URLs放入带抓取队列
		for(int i = 1; i <=100; i ++){
			urlQueueLevelOne.addElement("http://su.ganji.com/fang1/o" + i);
		}
		System.out.println(urlQueueLevelOne.getUrlQueue());
		
		if(!urlQueueLevelOne.isEmpty()){
			System.out.println("获取二级页面URL");
			GanJiUrl urlParser=new GanJiUrl(urlQueueLevelOne);
			urlParser.getUrlByParse();
		}
	}
	
	/**
	 * 准备初始的爬取链接  58同城
	 */
	private static void initializeQueueFiveEight(){
		// 赶集网租房250个一级页面，根据链接规则生成二级URLs放入带抓取队列
		for(int i = 1; i <=70; i ++){
			urlQueueLevelOne.addElement("http://su.58.com/chuzu/pn" + i);
		}
		System.out.println(urlQueueLevelOne.getUrlQueue());
		
		if(!urlQueueLevelOne.isEmpty()){
			System.out.println("获取二级页面URL");
			FiveEightUrl urlParser=new FiveEightUrl(urlQueueLevelOne);
			urlParser.getUrlByParse();
		}
	}
	/**
	 * 准备初始的爬取链接  安居客
	 */
	private static void initializeQueueAnJuKe(){
		// 赶集网租房250个一级页面，根据链接规则生成二级URLs放入带抓取队列
		for(int i = 1; i <=250; i ++){
			urlQueueLevelOne.addElement("http://su.zu.anjuke.com/fangyuan/p"+i+"-px3/#filtersort");
		}
		System.out.println(urlQueueLevelOne.getUrlQueue());
		
		if(!urlQueueLevelOne.isEmpty()){
			System.out.println("获取二级页面URL");
			AnJuKeUrl urlParser=new AnJuKeUrl(urlQueueLevelOne);
			urlParser.getUrlByParse();
		}
	}
	
	/**
	 * 准备初始小区的爬取链接
	 */
	private static void initializeQueueCOM(){
		// 赶集网租房250个一级页面，根据链接规则生成二级URLs放入带抓取队列
		for(int i = 0; i <=2000; i=i+20){
			urlQueueLevelOne.addElement("http://su.ganji.com/xiaoquzufang/f"+i+"/");
		}
		System.out.println(urlQueueLevelOne.getUrlQueue());
		
		if(!urlQueueLevelOne.isEmpty()){
			System.out.println("获取二级页面URL");
			GanJIComURL urlParser=new GanJIComURL(urlQueueLevelOne);
			urlParser.getUrlByParse();
		}
	}
}
