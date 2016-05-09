package cn.zju.yuki.spider.queue;

import java.util.LinkedList;

public class UrlQueue {
	// 待访问的url队列 
	private  LinkedList<String> urlQueue = new LinkedList<String>();

	public LinkedList<String> getUrlQueue(){
		return urlQueue;
	}
	
	public void setUrlQueue(UrlQueue queue){
		this.urlQueue.addAll(queue.getUrlQueue());
	}
	
	public synchronized  void addElement(String url){
		urlQueue.add(url);
	}
	
	public synchronized  void addFirstElement(String url){
		urlQueue.addFirst(url);
	}
	
	public synchronized  String outElement(){
		return urlQueue.removeFirst();
	}
	
	public synchronized  boolean isEmpty(){
		return urlQueue.isEmpty();
	}
	
	public int size(){
		return urlQueue.size();
	}
	
	public boolean isContains(String url){
		return urlQueue.contains(url);
	}
}
