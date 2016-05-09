package com.edu.suda.housing.match;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.*;

import test.EditDistanceSimilarityFunction;

import database.DBconnection;
import file.MyTime;
import house.House;

//import com.edu.suda.housing.match.BucketsInMatch.TempHouseInfo;
import com.edu.suda.housing.match.BucketsInMatch1.HouseInfo;
import com.edu.suda.housing.tfidf.*;
import com.edu.suda.housing.tools.Cnk;
import com.edu.suda.housing.tools.PrintData;
public class BucketsInMatch {
	
	public DBconnection db = DBconnection.getDbConnection();
	//public LinkedList <House> houseList = new LinkedList<House>();
	public LinkedList <House> innerHouse = new LinkedList<House>();
	
	public Map<String, LinkedList<House>> buckets = new HashMap<String, LinkedList<House>>();
	public HashMap<Integer,HouseInfo> houseInfo = new HashMap<Integer,HouseInfo>();
	public HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>> smallBuckets = new HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>>();
	public HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>> smallNullBuckets = new HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>>();
	public HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>> smallNoNullBuckets = new HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>>();
	
	public Map<String, LinkedList<House>> noneNullCode = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> nullCode = new HashMap<String, LinkedList<House>>();
	
	EditDistanceSimilarityFunction sim = new EditDistanceSimilarityFunction();	
 
	CountIFIDF cii = new CountIFIDF();
	//The number of all records and matched records
	int allRecordsNum = 0;
	int matchedRecordsNum = 0;
	int firstMatchedNum = 0;
	public static Double thresholdOne = 0.55;
	public static Double thresholdTwo = 0.2;
	public int isRerfresh = 0;
	
	//data, bj_ganji, cd_ganji, su_ganji, sz_ganji, tj_ganji
    public static String tableName = "su_ganji";
	public static String sql = "select * from "+ tableName;
	//bj_example, bj_result, cd_result, su_result, sz_result, tj_result
	File file=new File("results/su_result.txt");
	
	/**
	 * get the house buckets based on code
	 * */
	public Map<String, LinkedList<House>> getBuckets(){
		try{
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				allRecordsNum++;
				if(!buckets.containsKey(rs.getString("编码"))){
					buckets.put(rs.getString("编码"), new LinkedList<House>());
					buckets.get(rs.getString("编码")).add(new House(rs));
				}else{
					buckets.get(rs.getString("编码")).add(new House(rs));
				}
			}
			rs.close();
		}catch(Exception e){
			System.out.println("database read exception"+e.toString());
		}
		return buckets;
	}
	
	/**
	 * count up the number of different url
	 * */
	public HashSet<String> getUrlType(String mycode){
		HashSet<String> url = new HashSet<String>();
		for(String code: buckets.keySet()){
			if(code.equals(mycode)){
				for(House h : buckets.get(mycode)){
					url.add(h.url);
				}
			}
		}
		return url;
	}
	
	/**
	 * get the more accuracy house buckets based on url on the foundation of code buckets
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * */
	public HashMap<String, LinkedList<HashMap<String, LinkedList<House>>>> getSmallBuckets() throws UnsupportedEncodingException, IOException{
	  for(String code: buckets.keySet()){
		  HashSet<String> urls = getUrlType(code); 
		  LinkedList<HashMap<String, LinkedList<House>>> mapList = new LinkedList<HashMap<String,LinkedList<House>>>();
		  for(String url : urls){
			  HashMap<String, LinkedList<House>> urlbuckets = new HashMap<String, LinkedList<House>>();
		       for(House h : buckets.get(code)){
				  if(url.equals(h.url)){
					  if(!urlbuckets.containsKey(h.url)){
							urlbuckets.put(h.url, new LinkedList<House>());
							urlbuckets.get(h.url).add(h);
						}else{
							urlbuckets.get(h.url).add(h);
						}
			        }
			     } 
		       if(!urlbuckets.isEmpty()){
		    	   if(urlbuckets.get(url).size()>1){
		    		   firstMatchedNum+=Cnk.cnk(urlbuckets.get(url).size(), 2);
		    	   }
				  }else{
					  firstMatchedNum+=0;
				  }
		       for(int i=0; i<urlbuckets.get(url).size();i++){
		    	   for(int j=0; j<urlbuckets.get(url).size(); j++){
		    		   if(i<j){
//		    			   System.out.println("code: "+code+"  id1:  "+urlbuckets.get(url).get(i).getId()+"  id2:  "+urlbuckets.get(url).get(j).getId()+"   url1: "+urlbuckets.get(url).get(i).getUrl()+"   url2: "+urlbuckets.get(url).get(j).getUrl());
		    			   if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
				   		   FileOutputStream out = new FileOutputStream(file,true);
					   		StringBuffer sb = new StringBuffer();
			    			sb.append(urlbuckets.get(url).get(i).getId()+" "+urlbuckets.get(url).get(j).getId()+"\n");
			    			out.write(sb.toString().getBytes("utf-8"));
			    			out.close();
		    		   }
		    	   }	 		 
		 	  }
		 	  System.out.println("----------------");
		    mapList.add(urlbuckets);
		 }
		smallBuckets.put(code, mapList);
	  System.out.println("初次匹配的房源记录个数为为："+firstMatchedNum+"  总的记录个数为："+ allRecordsNum+"  匹配的比率为："+firstMatchedNum*1.0/allRecordsNum);
	  }
//	  for(String code:smallBuckets.keySet()){
//			for(HashMap<String, LinkedList<House>> map: smallBuckets.get(code)){
//				for(String url: map.keySet()){
//					System.out.println("url: "+url);
//					LinkedList<House> list = map.get(url);
//					for(House h: list){
//						System.out.println("id: "+h.id+"  code:"+h.code+"  ur:"+h.url);
//					}
//				}
//				System.out.println("小桶分割------------------");
//			}
//			System.out.println("大桶分割*************************************");
//		}
	  return smallBuckets;
}
		
	//存储待比较的房源信息
	class HouseInfo{
		public int id;
		public String code;
		public HashMap<String, Integer> words;
		public boolean isMatched;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public HouseInfo(){
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public HashMap<String, Integer> getWords() {
			return words;
		}
		public void setWords(HashMap<String, Integer> words) {
			this.words = words;
		}
		public boolean isMatched() {
			return isMatched;
		}
		public void setMatched(boolean isMatched) {
			this.isMatched = isMatched;
		}
	}
	
	/**
	 * compute the similarity of inner buckets
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * */
	public int innerCodesSimilarity() throws UnsupportedEncodingException, IOException{	
		/*将第一步得到的匹配数目赋给matchedRecordsNum*/
		matchedRecordsNum = firstMatchedNum;
    	//根据计算所得的相似度和设定的阈值比较
		for(String code: smallBuckets.keySet()){
			LinkedList<HashMap<String, LinkedList<House>>> codeBucketsList = smallBuckets.get(code);
			
			String sqlCode= "select *from "+tableName+" where 编码= '"+code+"'";
			HashMap<Integer, HashMap <String, Integer>> nomalTF = new HashMap<Integer, HashMap <String, Integer>>();
			HashMap<Integer, String[]> cutwords = new HashMap<Integer, String[]>();
			try {
				cutwords = cii.cutWord(cii.getText(sqlCode));
				nomalTF = cii.normalTF(cutwords);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.toString());
				e.printStackTrace();
			}
			//计算每个url桶下每个id中每个单词出现的数目
			for(HashMap<String, LinkedList<House>> urlBuckets:codeBucketsList){
				for(String url: urlBuckets.keySet()){
					//记录一个桶内部的每个词的出现次数
					HashMap<String, Integer> singleWordsNum = new HashMap<String, Integer>();
					HashMap <String, Integer> wordsNum = new HashMap <String, Integer>();
					LinkedList<House> huoseList = urlBuckets.get(url);
					for(House house: huoseList){
						wordsNum = nomalTF.get(house.id);
						for(String word: wordsNum.keySet()){
							if(singleWordsNum.containsKey(word)){
								singleWordsNum.put(word, singleWordsNum.get(word)+wordsNum.get(word));
							}else{
								singleWordsNum.put(word, wordsNum.get(word));
							}
						}
						HouseInfo hinfo = new HouseInfo();
						hinfo.setId(house.id);
						hinfo.setCode(house.code);
						hinfo.setWords(singleWordsNum);
						hinfo.setMatched(false);
						if(house != null){
							houseInfo.put(house.id, hinfo);
					   }
					}
				}
			}
			/*计算两个房源信息的相似度*/
			for(int i=0; i<codeBucketsList.size(); i++){
				HashMap<String, LinkedList<House>> bucketsmap0 = codeBucketsList.get(i);
				for(int j=0; j<codeBucketsList.size(); j++){
					HashMap<String, LinkedList<House>> bucketsmap1 = codeBucketsList.get(j);
					if(i<j  && !bucketsmap0.keySet().isEmpty() && !bucketsmap1.keySet().isEmpty()){
						House house0 = null, house1 = null;
						for(String url0:bucketsmap0.keySet()){
							LinkedList<House> houseList0 = bucketsmap0.get(url0);
							house0 = houseList0.get(0);
							break;
						}
						for(String url1:bucketsmap1.keySet()){
							LinkedList<House> houseList1 = bucketsmap1.get(url1);
							house1 = houseList1.get(0);
							break;
						}
						HouseInfo hinfo0 = houseInfo.get(house0.id);
						HouseInfo hinfo1 = houseInfo.get(house1.id);
						double flag = similarity(hinfo0,hinfo1);
						if(flag>thresholdOne){
							matchedRecordsNum++;
//							System.out.println("code1   "+hinfo0.getId()+"   code2   "+hinfo1.getId()+"  flag  "+flag);
							if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
					   		   FileOutputStream out = new FileOutputStream(file,true);
						   		StringBuffer sb = new StringBuffer();
				    			sb.append(house0.id+" "+house1.id+"\n");
				    			out.write(sb.toString().getBytes("utf-8"));
				    			out.close();
						}
					}
				}
			}
		}
		System.out.println("总的记录条数： "+allRecordsNum+"  匹配的记录条数："+matchedRecordsNum+"  匹配的比率："+matchedRecordsNum*1.0/allRecordsNum);
        return matchedRecordsNum;
	}
	
	/**
	 * Part the smallbuckets into two kinds of buckets: with null and without null
     */
	public void partBuckets(){
		for(String code: smallBuckets.keySet()){
			LinkedList<HashMap<String, LinkedList<House>>> houseListNo = new LinkedList<HashMap<String,LinkedList<House>>>();
			LinkedList<HashMap<String, LinkedList<House>>> houseListNull = new LinkedList<HashMap<String,LinkedList<House>>>();
			if(code.contains("null")){
				houseListNull = smallBuckets.get(code);
				smallNullBuckets.put(code, houseListNull);
			}else{
				houseListNo = smallBuckets.get(code);
				smallNoNullBuckets.put(code, houseListNo);
			}
		}
//		for(String code:smallNullBuckets.keySet()){
//		  System.out.println("code"+code+"  smallBuckets:"+smallNullBuckets.get(code));
//	}
 }
	class CompareBuckets{
		String codeNull;
		String codeNonull;
		public String getCodeNull() {
			return codeNull;
		}
		public void setCodeNull(String codeNull) {
			this.codeNull = codeNull;
		}
		public String getCodeNonull() {
			return codeNonull;
		}
		public void setCodeNonull(String codeNonull) {
			this.codeNonull = codeNonull;
		}
	}
	/**
	 * Get the most similar buckets which don't contain missing values
	 * */
	public LinkedList <CompareBuckets> getNearestNonullBuckets(){
		LinkedList <CompareBuckets> compareList = new LinkedList<BucketsInMatch.CompareBuckets>();
		for(String code: smallNullBuckets.keySet()){
			String[] nullcode = code.split("-");
			for(String code1: smallNoNullBuckets.keySet()){
				boolean flag = true;
				String[] nonullcode = code1.split("-");
				for(int i=0; i<nullcode.length; i++){
					if(nullcode[i].equals(nonullcode[i]) || nullcode[i].equals("null")){
						continue;
					}else{
						flag = false;
					}
				}
				if(flag == true){
					//使不带有null的smallbucket与带有null的bucket中的每个smallbucket计算相似度
					CompareBuckets cb = new CompareBuckets();
					cb.setCodeNonull(code1);
					cb.setCodeNull(code);
					compareList.add(cb);
			}
		}
	}
		System.out.println("yes");
   return compareList;
 }
		
	/**
	 * compute the similarity of inner buckets
	 * */
	public int outerCodesSimilarity(){
		LinkedList <CompareBuckets> compareList = getNearestNonullBuckets();
//		System.out.println("size:-------------"+compareList.size());
		for(CompareBuckets cb: compareList){
			String code0 = cb.getCodeNull();
			String code1 = cb.getCodeNonull();
//            System.out.println("code0： "+code0+" code1"+code1);
			LinkedList<HashMap<String, LinkedList<House>>> codeNullBucketsList = smallBuckets.get(code0);
			String sqlCode0= "select *from "+tableName+" where 编码= '"+code0+"'";
			HashMap<Integer, HashMap <String, Integer>> nomalTF0 = new HashMap<Integer, HashMap <String, Integer>>();
			HashMap<Integer, String[]> cutwords0 = new HashMap<Integer, String[]>();
			try {
				cutwords0 = cii.cutWord(cii.getText(sqlCode0));
				nomalTF0 = cii.normalTF(cutwords0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.toString());
				e.printStackTrace();
			}
			
			LinkedList<HashMap<String, LinkedList<House>>> codeNonullBucketsList = smallBuckets.get(code1);
			String sqlCode1= "select *from "+tableName+" where 编码= '"+code1+"'";
			HashMap<Integer, HashMap <String, Integer>> nomalTF1 = new HashMap<Integer, HashMap <String, Integer>>();
			HashMap<Integer, String[]> cutwords1 = new HashMap<Integer, String[]>();
			try {
				cutwords1 = cii.cutWord(cii.getText(sqlCode1));
				nomalTF1 = cii.normalTF(cutwords1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.toString());
				e.printStackTrace();
			}
			
			HashMap<Integer,HouseInfo> houseInfo0 = new HashMap<Integer,HouseInfo>();
			HashMap<Integer,HouseInfo> houseInfo1 = new HashMap<Integer,HouseInfo>();
			
			//计算每个url桶下每个id中每个单词出现的数目
			for(HashMap<String, LinkedList<House>> urlBuckets:codeNullBucketsList){
				for(String url: urlBuckets.keySet()){
					//记录一个桶内部的每个词的出现次数
					HashMap<String, Integer> singleWordsNum = new HashMap<String, Integer>();
					HashMap <String, Integer> wordsNum = new HashMap <String, Integer>();
					LinkedList<House> huoseList = urlBuckets.get(url);
					for(House house: huoseList){
						wordsNum = nomalTF0.get(house.id);
						for(String word: wordsNum.keySet()){
							if(singleWordsNum.containsKey(word)){
								singleWordsNum.put(word, singleWordsNum.get(word)+wordsNum.get(word));
							}else{
								singleWordsNum.put(word, wordsNum.get(word));
							}
						}
						HouseInfo hinfo = new HouseInfo();
						hinfo.setId(house.id);
						hinfo.setCode(house.code);
						hinfo.setWords(singleWordsNum);
						hinfo.setMatched(false);
						if(house != null){
							houseInfo0.put(house.id, hinfo);
					   }
					}
				}
			}
			
			for(HashMap<String, LinkedList<House>> urlBuckets:codeNonullBucketsList){
				for(String url: urlBuckets.keySet()){
					//记录一个桶内部的每个词的出现次数
					HashMap<String, Integer> singleWordsNum = new HashMap<String, Integer>();
					HashMap <String, Integer> wordsNum = new HashMap <String, Integer>();
					LinkedList<House> huoseList = urlBuckets.get(url);
					for(House house: huoseList){
						wordsNum = nomalTF1.get(house.id);
						for(String word: wordsNum.keySet()){
							if(singleWordsNum.containsKey(word)){
								singleWordsNum.put(word, singleWordsNum.get(word)+wordsNum.get(word));
							}else{
								singleWordsNum.put(word, wordsNum.get(word));
							}
						}
						HouseInfo hinfo = new HouseInfo();
						hinfo.setId(house.id);
						hinfo.setCode(house.code);
						hinfo.setWords(singleWordsNum);
						hinfo.setMatched(false);
						if(house != null){
							houseInfo1.put(house.id, hinfo);
					   }
					}
				}
			}
			
			/*计算两个房源信息的相似度*/
			for(int i=0; i<codeNullBucketsList.size(); i++){
				HashMap<String, LinkedList<House>> bucketsmap0 = codeNullBucketsList.get(i);
				for(int j=0; j<codeNonullBucketsList.size(); j++){
					HashMap<String, LinkedList<House>> bucketsmap1 = codeNonullBucketsList.get(j);
					if(!bucketsmap0.keySet().isEmpty() && !bucketsmap1.keySet().isEmpty()){
						House house0 = null, house1 = null;
						for(String url0:bucketsmap0.keySet()){
							LinkedList<House> houseList0 = bucketsmap0.get(url0);
							house0 = houseList0.get(0);
							break;
						}
						for(String url1:bucketsmap1.keySet()){
							LinkedList<House> houseList1 = bucketsmap1.get(url1);
							house1 = houseList1.get(0);
							break;
						}
						HouseInfo hinfo0 = houseInfo0.get(house0.id);
						HouseInfo hinfo1 = houseInfo1.get(house1.id);
						double flag = similarity(hinfo0,hinfo1);
						if(flag>thresholdTwo){
							matchedRecordsNum++;
//							System.out.println("nullCode   "+hinfo0.getId()+"   Nonullcode   "+hinfo1.getId()+"  flag  "+flag);
						    //更新之前的smallbuckets，将带有Null的buckets插入到非null的buckets中
							for(String url0:bucketsmap0.keySet()){
								LinkedList<House> houseList0 = bucketsmap0.get(url0);
								for(House h: houseList0){
									h.setCode(house1.code);
								}
								bucketsmap0.get(url0).clear();
								bucketsmap0.put(url0, houseList0);
							}
							codeNonullBucketsList.add(bucketsmap0);
						}
					}
				}
			}
		}
		System.out.println("总的记录条数： "+allRecordsNum+"  匹配的记录条数："+matchedRecordsNum+"  匹配的比率："+matchedRecordsNum*1.0/allRecordsNum);
        return matchedRecordsNum;
	}	
	
	/**
	 * Based on cosine vectors，computer the similarity
	 * */
	public double similarity(HouseInfo info1, HouseInfo info2){
			HashSet<String> wordSet = new HashSet<String>();
			HashMap<String, Integer> singleWordsNum1 = new HashMap<String, Integer>();
			HashMap<String, Integer> singleWordsNum2 = new HashMap<String, Integer>();
			ArrayList<Integer> vector1 = null;
	    	ArrayList<Integer> vector2 = null;
			vector1 = new ArrayList<Integer>();
			vector2 = new ArrayList<Integer>();
	    	vector1.clear();
	    	vector2.clear();
	    	double similarity = 0.0;
	    	double numcrator = 0.0;
	    	double denominator = 1.0;
	    	double temp1 =0.0, temp2 = 0.0;
				if(info1.getCode().equals(info2.getCode())){
					singleWordsNum1 = info1.getWords();
					singleWordsNum2 = info2.getWords();
					for(String word1: singleWordsNum1.keySet()){
						wordSet.add(word1);
					}
					for(String word2: singleWordsNum1.keySet()){
						wordSet.add(word2);
					}
					for(String w1: wordSet){
						if(singleWordsNum1.containsKey(w1)){
							vector1.add(singleWordsNum1.get(w1));
						}else{
							vector1.add(0);
						}
					}
					for(String w2: wordSet)
					{
						if(singleWordsNum2.containsKey(w2)){
							vector2.add(singleWordsNum1.get(w2));
						}else{
							vector2.add(0);
						}
					}				
			    	for(int k = 0; k<wordSet.size(); k++){
			    		numcrator += vector1.get(k)*vector2.get(k);
			    		temp1 += Math.pow(vector1.get(k), 2.0);
			    		temp2 += Math.pow(vector2.get(k), 2.0);
			    	}
			    	denominator = Math.sqrt(temp1)*Math.sqrt(temp2);
			    	similarity = numcrator/denominator;	
				}
	  return similarity;
  }
  
	/**
	 * compute the similarity of inner buckets without null
	 * */
	public int innerMoreCodesSimilarity(){	
		//更多匹配的实体个数
		int newMatchedNum = 0;
    	//根据计算所得的相似度和设定的阈值比较
		for(String code: smallNoNullBuckets.keySet()){
			LinkedList<HashMap<String, LinkedList<House>>> codeBucketsList = smallNoNullBuckets.get(code);
			
			String sqlCode= "select *from "+tableName+" where 编码= '"+code+"'";
			HashMap<Integer, HashMap <String, Integer>> nomalTF = new HashMap<Integer, HashMap <String, Integer>>();
			HashMap<Integer, String[]> cutwords = new HashMap<Integer, String[]>();
			try {
				cutwords = cii.cutWord(cii.getText(sqlCode));
				nomalTF = cii.normalTF(cutwords);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.toString());
				e.printStackTrace();
			}
			//计算每个url桶下每个id中每个单词出现的数目
			for(HashMap<String, LinkedList<House>> urlBuckets:codeBucketsList){
				for(String url: urlBuckets.keySet()){
					//记录一个桶内部的每个词的出现次数
					HashMap<String, Integer> singleWordsNum = new HashMap<String, Integer>();
					HashMap <String, Integer> wordsNum = new HashMap <String, Integer>();
					LinkedList<House> huoseList = urlBuckets.get(url);
					for(House house: huoseList){
						wordsNum = nomalTF.get(house.id);
						for(String word: wordsNum.keySet()){
							if(singleWordsNum.containsKey(word)){
								singleWordsNum.put(word, singleWordsNum.get(word)+wordsNum.get(word));
							}else{
								singleWordsNum.put(word, wordsNum.get(word));
							}
						}
						HouseInfo hinfo = new HouseInfo();
						hinfo.setId(house.id);
						hinfo.setCode(house.code);
						hinfo.setWords(singleWordsNum);
						hinfo.setMatched(false);
						if(house != null){
							houseInfo.put(house.id, hinfo);
					   }
					}
				}
			}
			/*计算两个房源信息的相似度*/
			for(int i=0; i<codeBucketsList.size(); i++){
				HashMap<String, LinkedList<House>> bucketsmap0 = codeBucketsList.get(i);
				for(int j=0; j<codeBucketsList.size(); j++){
					HashMap<String, LinkedList<House>> bucketsmap1 = codeBucketsList.get(j);
					if(i<j  && !bucketsmap0.keySet().isEmpty() && !bucketsmap1.keySet().isEmpty()){
						House house0 = null, house1 = null;
						for(String url0:bucketsmap0.keySet()){
							LinkedList<House> houseList0 = bucketsmap0.get(url0);
							house0 = houseList0.get(0);
							break;
						}
						for(String url1:bucketsmap1.keySet()){
							LinkedList<House> houseList1 = bucketsmap1.get(url1);
							house1 = houseList1.get(0);
							break;
						}
						HouseInfo hinfo0 = houseInfo.get(house0.id);
						HouseInfo hinfo1 = houseInfo.get(house1.id);
						double flag = similarity(hinfo0,hinfo1);
						if(flag>thresholdOne){
							newMatchedNum++;
							matchedRecordsNum++;
//							System.out.println("code1   "+hinfo0.getId()+"   code2   "+hinfo1.getId()+"  flag  "+flag);
							}
					}
				}
			}
		}
//		System.out.println("总的记录条数： "+allRecordsNum+"  匹配的记录条数："+matchedRecordsNum+"  匹配的比率："+matchedRecordsNum*1.0/allRecordsNum);
        return matchedRecordsNum;
	}
	
	/**
	 * refresh the buckets without null to find out more matched entities by the process of interaction
	 * */
	public void interaction(){
		innerMoreCodesSimilarity();
	}
	
//	//实现存在空编码的桶之间的更新的交互过程
//		public void refreshNullInteraction(){
//			int num = 0;
//			while(true){
//				if(houseInfo.size() == 0){
//					refreshNullCode();
//				}
//				//统计目前的已更新的个数
////				for(HouseInfo info: houseInfo){
////					if(info.isMatched()){
////						num++;
////					}
////				}
//				if(num == houseInfo.size()) return;
//				if(num<houseInfo.size()){
//					refreshNullCode();
//				}
//			}
//		}
		
	public static void main(String[]args) throws UnsupportedEncodingException, IOException{
		//String sql = "select * from test_pre_ganji1 group by 编码";
		//String sql = "select * from test_pre_ganji1";		
		BucketsInMatch bim = new BucketsInMatch();
		long before = System.currentTimeMillis();
		bim.getBuckets();
		bim.getSmallBuckets();
		bim.innerCodesSimilarity();
		bim.partBuckets();
		bim.outerCodesSimilarity();
		long current = System.currentTimeMillis();
		MyTime.gettime(current-before);
		//System.out.println(bim.getBuckets().size());
		//bim.SortBucketsByNull();
		//bim.getNearestBuckets();
		//System.out.println(bim.getSmallBuckets());
		//bim.getTempInfo();
		//bim.sortByHouseCode();
		//bim.refreshNullCode();
		//bim.RefreshSameCode();
		//bim.refreshInteraction();
		//bim.SortBucketsByNull();
		//bim.refreshNullCode();
		//bim.freshInBuckets();
		//bim.getNullAndNoneBuckets(sql);
		//System.out.println(bim.getSmallBuckets(bim.getBuckets(sql, sql1)));
		//System.out.println(bim.dataTwoHouse(sql).size());
	}
}