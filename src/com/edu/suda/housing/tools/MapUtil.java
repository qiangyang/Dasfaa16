package com.edu.suda.housing.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtil {
	public enum SortType{
		ASC,DESC
	}
	public static int TOPK = 30;
	/**
	 * 根据 map 的值进行排序
	 * 
	 * @param map 需要排序的map
	 * @param type 排序方式
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final SortType type) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				switch (type) {
				case ASC:
					return (o1.getValue()).compareTo(o2.getValue());
				default:
					return (o2.getValue()).compareTo(o1.getValue());
				}
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		int i = 0;
		for (Map.Entry<K, V> entry : list) {
			if(i < TOPK){
				result.put(entry.getKey(), entry.getValue());
				i++;
			}
			else{
				break;
			}
		}
		System.out.println("k"+TOPK);
		return result;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueTopK(Map<K, V> map, final int TOPK) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
					return (o2.getValue()).compareTo(o1.getValue());				
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		int i = 0;
		for (Map.Entry<K, V> entry : list) {
			if(i < TOPK){
				result.put(entry.getKey(), entry.getValue());
				i++;
			}
			else{
				break;
			}
		}
		return result;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
					return (o2.getValue()).compareTo(o1.getValue());				
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	public static void main(String[] args) {
		Map<String, Integer> test = new HashMap<String, Integer>();
		test.put("a", 100);
		test.put("b", 1);
		test.put("c", 1001);
		System.out.println(MapUtil.sortByValue(test, SortType.ASC));
	}
}
