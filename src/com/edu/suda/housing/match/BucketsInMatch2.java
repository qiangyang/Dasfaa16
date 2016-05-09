package com.edu.suda.housing.match;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

import test.EditDistanceSimilarityFunction;

import database.DBconnection;
import house.House;

import com.edu.suda.housing.tfidf.*;
import com.edu.suda.housing.tools.PrintData;
public class BucketsInMatch2 {
	
	public DBconnection db = DBconnection.getDbConnection();
	public LinkedList <House> houseList = new LinkedList<House>();
	public LinkedList <House> innerHouse = new LinkedList<House>();
	
	public Map<String, LinkedList<House>> buckets = new HashMap<String, LinkedList<House>>();
	public LinkedList<TempHouseInfo> houseTemp = new LinkedList<TempHouseInfo>();
	//public LinkedList<HashMap<String, LinkedList<House>>> newBUckets = new LinkedList<HashMap<String, LinkedList<House>>>();
	HashMap<String, LinkedList<House>> urlbuckets = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> noneNullCode = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> nullCode = new HashMap<String, LinkedList<House>>();
	
	EditDistanceSimilarityFunction sim = new EditDistanceSimilarityFunction();	
 
	CountIFIDF cii = new CountIFIDF();
	
	public LinkedList<Integer> add = new LinkedList<Integer>();
	public LinkedList<Integer> del = new LinkedList<Integer>();
	
	public static String sql = "select * from test_pre_ganji1";
	public static Double threshold = 0.5;
	//get the house object from datasource
	public LinkedList<House> dataTwoHouse(String sql){
		try{
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				House house = new House(rs);
				houseList.add(house);
			}
			rs.close();
		}catch(Exception e){
			System.out.println("database read exception");
		}
		return houseList;
	}
	//get the house buckets
	public Map<String, LinkedList<House>> getBuckets(){
		try{
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
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
	
	//part the buckets into smaller based on url/title
	//public LinkedList<HashMap<String, LinkedList<House>>> getSmallBuckets(){
	public HashMap<String, LinkedList<House>> getSmallBuckets(){	
	  for(String code: buckets.keySet()){
	 		//HashMap<String, LinkedList<House>> urlbuckets = new HashMap<String, LinkedList<House>>();
				for(House h : buckets.get(code)){
					if(!urlbuckets.containsKey(h.url)){
						urlbuckets.put(h.url, new LinkedList<House>());
						urlbuckets.get(h.url).add(h);
					}else{
						urlbuckets.get(h.url).add(h);
					}
				}
				//newBUckets.add(urlbuckets);
			}
		return urlbuckets;
		//return newBUckets;
	}
	
	//存储房源临时信息的三元组
	class TempHouseInfo{
		public String bucketsId;
		public LinkedList<House> house;
		public LinkedList<Integer> houseId;
		
			public String getBucketsId() {
				return bucketsId;
			}
			public void setBucketsId(String bucketsId) {
				this.bucketsId = bucketsId;
			}
			public LinkedList<House> getHouse() {
				return house;
			}
			public void setHouse(LinkedList<House> house) {
				this.house = house;
			}
			public LinkedList<Integer> getHouseId() {
				return houseId;
			}
			public void setHouseId(LinkedList<Integer> houseId) {
				this.houseId = houseId;
			}	 
	}
	//融合编码桶的信息和url桶的信息
	public LinkedList<TempHouseInfo> getTempInfo(){
		for(String code : buckets.keySet()){
			for(String u : urlbuckets.keySet()){
				TempHouseInfo temp = new TempHouseInfo();
				LinkedList<House> houseUrl = new LinkedList<House>();
				LinkedList<Integer> houseId = new LinkedList<Integer>();
				for(House h1 : buckets.get(code)){
					for(House h2 : urlbuckets.get(u)){
						if(h1.id == h2.id){
							houseUrl.add(h1);
							houseId.add(h1.id);
							break;
						}
					}
				}
				temp.setBucketsId(code);
				temp.setHouse(houseUrl);
				temp.setHouseId(houseId);
				houseTemp.add(temp);
			}
		}
		for(TempHouseInfo tem: houseTemp){
			System.out.println("bid=========:"+tem.getBucketsId()+"  hid======:"+tem.getHouseId());
			PrintData.print(tem.getHouse());
		}
		return houseTemp;
	}
	
	//删除桶中的hid为空的信息
		public LinkedList<TempHouseInfo> removeNull(){
			for(int i = 0; i<houseTemp.size(); i++){
				TempHouseInfo t = houseTemp.get(i);
				LinkedList<Integer> llist = new LinkedList<Integer>();
				llist = t.getHouseId();
				if(llist.size() == 0){
					houseTemp.remove(t);
				}
			}
			return houseTemp;
		}
   
	//根据设定的相似度阈值，将大于阈值的在不同筒的房源更新至同一个筒中
	public LinkedList<TempHouseInfo> freshInBuckets(){	
		int times = 0;
		for(int i = 0; i<houseTemp.size(); i++){
			TempHouseInfo t = houseTemp.get(i);
			String bid = t.getBucketsId();
			for(int j = 0; j<houseTemp.size(); j++){
				TempHouseInfo t1 = houseTemp.get(j);
				String bid1 = t1.getBucketsId();
				if(bid.equals(bid1) && i!=j){
					LinkedList<Integer> l1 = new LinkedList<Integer>();
					LinkedList<Integer> l2 = new LinkedList<Integer>();
					l1 = t.getHouseId();
					l2 = t1.getHouseId();
					if(isRefreshBuckets(l1,l2)){
						l1.addAll(add);
						l2.removeAll(del);						
						System.out.println("当前的桶更新了"+(times++));
					}
				}
			}
		}
		removeNull();
		for(TempHouseInfo tem: houseTemp){
			System.out.println("bid:"+tem.getBucketsId()+"  hid:"+tem.getHouseId());
			PrintData.print(tem.getHouse());
		}
		return houseTemp;
	}
	//是否更新桶的信息
	public boolean isRefreshBuckets(LinkedList<Integer> l1,LinkedList<Integer> l2){
		Map<Integer, HashMap<String, Float>> tfidf = new HashMap<Integer, HashMap<String, Float>>();
		HashMap<String, Float> vectorWords1 = new  HashMap<String, Float>();
		HashMap<String, Float> vectorWords2 = new  HashMap<String, Float>();
		HashMap <String, Integer> words1Num = new HashMap <String, Integer>();
		HashMap <String, Integer> words2Num = new HashMap <String, Integer>();
		
		HashMap <String, Integer> getWords1Num = new HashMap <String, Integer>();
		HashMap <String, Integer> getWords2Num = new HashMap <String, Integer>();
		HashMap<Integer, HashMap <String, Integer>> nomalTF = new HashMap<Integer, HashMap <String, Integer>>();
		
		tfidf = cii.getTFIDF(sql);
		HashMap<Integer, String[]> cutwords = null;
		
	    boolean refreshFlag = false;
		try {
			cutwords = cii.cutWord(cii.getText(sql));
			nomalTF = cii.normalTF(cutwords);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//================
		//遍历中删除元素的方法
		//ListIterator<Integer> lt1 = l1.listIterator();		
		//ListIterator<Integer> lt2 = l2.listIterator();
		//======================
		//for(int id : l1){
		for(int i=0;i<l1.size();i++){
			int id = l1.get(i);
		///	for(int id1 : l2){
		    for(int j=0; j<l2.size();j++){
		    	int id1 = l2.get(j);
//		  while(lt1.hasNext()){
//			  int id = (Integer) lt1.next();
//			  while(lt2.hasNext()){
//				  int id1 = (Integer) lt2.next();
				vectorWords1 = tfidf.get(id);
		    	words1Num = nomalTF.get(id);
		    	Set<String> wordSet = new HashSet<String>();
		    	for(String words : vectorWords1.keySet()){
		    		wordSet.add(words);
		    		getWords1Num.put(words, words1Num.get(words));
		    	}
		    	vectorWords2 = tfidf.get(id1);
		    	words2Num = nomalTF.get(id1);
    			for(String words : vectorWords2.keySet()){
		    		wordSet.add(words);
		    		getWords2Num.put(words, words2Num.get(words));
		    	}
    			ArrayList<Integer> vector1 = new ArrayList<Integer>();
		    	ArrayList<Integer> vector2 = new ArrayList<Integer>();
		    	for(String word: wordSet){
		    		int v1 = 0;
		    		//if(getWords1Num.get(word) != null){
		    			v1 = getWords1Num.get(word);
		    		//}
		    		int v2 = 0;
		    		//if(getWords2Num.get(word) != null){
		    			v2 = getWords2Num.get(word);
		    		//}
		    		vector1.add(v1);
		    		vector2.add(v2);
		    	}
		    	
		    	//System.out.println("vector1"+vector1+"    vector2"+vector2);
		    	double similarity = 0.0;
		    	double numcrator = 0.0;
		    	double denominator = 1.0;
		    	double temp1 =0.0, temp2 = 0.0;
		    	for(int k = 0; k<vector1.size(); k++){
		    		numcrator += vector1.get(k)*vector2.get(k);
		    		temp1 += Math.pow(vector1.get(k), 2.0);
		    		temp2 += Math.pow(vector2.get(k), 2.0);
		    	}
		    	denominator = Math.sqrt(temp1)*Math.sqrt(temp2);
		    	similarity = numcrator/denominator;	
		    	
		    	//根据计算所得的相似度和设定的阈值比较
		    	if(similarity >= threshold){
		    		refreshFlag = true;			    		
		    		try{
//		    			lt1.add(id1);
//		    			lt2.remove();
		    			if(!add.contains(id1)){
		    				add.add(id1);
		    			}
		    			if(!del.contains(id1)){
			    			del.add(id1);
		    			}
		    			}
		    		catch(Exception e){
		    			System.out.println(e.toString());
		    		}		    		
		    	}
			}
		}
		return refreshFlag;
	}
	
	public static void main(String[]args){
		//String sql = "select * from test_pre_ganji1 group by 编码";
		//String sql = "select * from test_pre_ganji1";
		BucketsInMatch2 bim = new BucketsInMatch2();
		System.out.println(bim.getBuckets().size());
		System.out.println(bim.getSmallBuckets());
		bim.getTempInfo();
		bim.freshInBuckets();
		//bim.getNullAndNoneBuckets(sql);
		//System.out.println(bim.getSmallBuckets(bim.getBuckets(sql, sql1)));
		//System.out.println(bim.dataTwoHouse(sql).size());
	}
}
