package com.edu.suda.housing.tools;

import java.util.LinkedList;
import java.util.Map;

public class PrintData {

	public static void print(LinkedList<?> list){
		int i = 0;
		for(Object obj: list){
			System.out.println(i+" 链表的结果为"+obj.toString());
			i++;
		}
	}
	
	public static void print(Map<Object,LinkedList<?>> map){
		int i = 0;
		for(Object obj: map.keySet()){
			for(Object o: map.get(obj)){
			System.out.println(i+"  当前对象为："+o);
			i++;
		  }
		}
	}
}
