package com.edu.suda.housing.tools;

import house.House;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.edu.suda.housing.match.BucketsInMatch;

public class Buckets {
    
	public Map<String, LinkedList<House>> noneNullCode = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> nullCode = new HashMap<String, LinkedList<House>>();
	
	public Buckets() {
		// TODO Auto-generated constructor stub
	}
//	public void getNullandNoneBuckets(Map<String, LinkedList<House>> buckets){
//		for(String i: buckets.keySet()){
//			LinkedList<House> nullHouse = new LinkedList<House>();
//			LinkedList<House> noneNullHouse = new LinkedList<House>();
//			if(i != "" && i.contains("null")){
//				for(House h: buckets.get(i)){
//					nullHouse.add(h);
//				}
//				nullCode.put(i, nullHouse);
//			}else{
//				for(House h: buckets.get(i)){
//					noneNullHouse.add(h);
//				}
//			  noneNullCode.put(i, noneNullHouse);
//			}
//		  }
//          System.out.println(nullCode+"\n"+noneNullCode);
//	}
	/*
	 * 将原先的桶根据编码是否有空值分为两大类：含空和不含空
	 * 
	 * */
	public Map<String, LinkedList<House>> getNullBuckets(Map<String, LinkedList<House>> buckets){	
		for(String i: buckets.keySet()){	
			LinkedList<House> nullHouse = new LinkedList<House>();
			if(i != "" && i.contains("null")){
				for(House h: buckets.get(i)){
					nullHouse.add(h);
				}
				nullCode.put(i, nullHouse);
			}
		  }
          return nullCode;
	}
	
	public Map<String, LinkedList<House>> getNoneBuckets(Map<String, LinkedList<House>> buckets){
		for(String i: buckets.keySet()){	
			if(i != "" && !i.contains("null")){
				LinkedList<House> noneNullHouse = new LinkedList<House>();	
				for(House h: buckets.get(i)){
					//System.out.println("h"+h.id);
					noneNullHouse.add(h);
				}
				noneNullCode.put(i, noneNullHouse);
			}
		  }
		 return noneNullCode;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Buckets b = new Buckets();
		BucketsInMatch bi = new BucketsInMatch();
//		System.out.println("before");
//		for(String code: bi.getBuckets().keySet()){
//		System.out.println(code+"  "+bi.getBuckets().get(code));
//	}
//		System.out.println("NullBuckets"+b.getNullBuckets(bi.getBuckets()));
//		System.out.println("middle");
//		for(String code: bi.getBuckets().keySet()){
//		System.out.println(code+"  "+bi.getBuckets().get(code));
//	}
//		System.out.println("NoneBuckets"+b.getNoneBuckets(bi.getBuckets()));
//		System.out.println("after");
//		for(String code: bi.getBuckets().keySet()){
//		System.out.println(code+"  "+bi.getBuckets().get(code));
//	}
	}

}
