package cn.zju.yuki.spider.queue;

import java.util.LinkedList;

public class VisitedUrlQueue {
	// 已访问的url队列 
	private   LinkedList<String> visitedUrlQueue = new LinkedList<String>();
	
	public LinkedList<String> getVisitedUrlQueue(){
		return visitedUrlQueue;
	}
	
	public void setVisitedUrlQueue(VisitedUrlQueue queue){
		this.visitedUrlQueue.addAll(queue.getVisitedUrlQueue());
	}

	public synchronized  void addElement(String url){
		visitedUrlQueue.add(url);
	}
	
	public synchronized  void addFirstElement(String url){
		visitedUrlQueue.addFirst(url);
	}
	
	public synchronized  String outElement(){
		return visitedUrlQueue.removeFirst();
	}
	
	public synchronized  boolean isEmpty(){
		return visitedUrlQueue.isEmpty();
	}
	
	public int size(){
		return visitedUrlQueue.size();
	}
	
	public boolean isContains(String url){
		return visitedUrlQueue.contains(url);
	}
}
