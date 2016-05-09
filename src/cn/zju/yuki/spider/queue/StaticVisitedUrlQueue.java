package cn.zju.yuki.spider.queue;

import java.util.LinkedList;

public class StaticVisitedUrlQueue {
	// 已访问的url队列 
	private static  LinkedList<String> visitedUrlQueue = new LinkedList<String>();
	
	public static LinkedList<String> getVisitedUrlQueue(){
		return visitedUrlQueue;
	}
	
	public synchronized static void addElement(String url){
		visitedUrlQueue.add(url);
	}
	
	public synchronized static void addFirstElement(String url){
		visitedUrlQueue.addFirst(url);
	}
	
	public synchronized static String outElement(){
		return visitedUrlQueue.removeFirst();
	}
	
	public synchronized static boolean isEmpty(){
		return visitedUrlQueue.isEmpty();
	}
	
	public static int size(){
		return visitedUrlQueue.size();
	}
	
	public static boolean isContains(String url){
		return visitedUrlQueue.contains(url);
	}
}
