package com.edu.suda.housing.match;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

import test.EditDistanceSimilarityFunction;

import database.DBconnection;
import house.House;

import com.edu.suda.housing.match.BucketsInMatch1.TempHouseInfo;
import com.edu.suda.housing.tfidf.*;
import com.edu.suda.housing.tools.PrintData;
public class BucketsInMatch1 {
	
	public DBconnection db = DBconnection.getDbConnection();
	public LinkedList <House> houseList = new LinkedList<House>();
	public LinkedList <House> innerHouse = new LinkedList<House>();
	
	public Map<String, LinkedList<House>> buckets = new HashMap<String, LinkedList<House>>();
	//public LinkedList<TempHouseInfo> houseTemp = new LinkedList<TempHouseInfo>();
	//public LinkedList<HouseInfo> houseInfo = new LinkedList<HouseInfo>();
	public HashMap<Integer,TempHouseInfo> houseTemp = new HashMap<Integer,TempHouseInfo>();
	public HashMap<Integer,HouseInfo> houseInfo = new HashMap<Integer,HouseInfo>();
	//public LinkedList<HashMap<String, LinkedList<House>>> newBUckets = new LinkedList<HashMap<String, LinkedList<House>>>();
	public HashMap<String, LinkedList<House>> urlbuckets = new HashMap<String, LinkedList<House>>();
	
	public Map<String, LinkedList<House>> noneNullCode = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> nullCode = new HashMap<String, LinkedList<House>>();
	
	public HashMap<Integer,TempHouseInfo> houseTempNull = new HashMap<Integer,TempHouseInfo>();
	public HashMap<Integer,TempHouseInfo> houseTempNone = new HashMap<Integer,TempHouseInfo>();
	
	EditDistanceSimilarityFunction sim = new EditDistanceSimilarityFunction();	
 
	CountIFIDF cii = new CountIFIDF();
	//The number of all records and matched records
	int allRecordsNum = 0;
	int matchedRecordsNum = 0;
	
//	public static String sql = "select * from test_pre_ganji1";
	public static String sql = "select * from data";
	public static Double threshold = 0.95;
	public int isRerfresh = 0;
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
				for(House h : buckets.get(code)){
					if(!urlbuckets.containsKey(h.url)){
						urlbuckets.put(h.url, new LinkedList<House>());
						urlbuckets.get(h.url).add(h);
					}else{
						urlbuckets.get(h.url).add(h);
					}
				}
			}
		return urlbuckets;
	}
	
	//存储房源临时信息的三元组
	class TempHouseInfo{
		public int id;
		public String bucketsId;
		public LinkedList<House> house;
		public LinkedList<Integer> houseId;
		
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
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
	public HashMap<Integer,TempHouseInfo> getTempInfo(){
		int id = 0;
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
				temp.setId(id);
				temp.setBucketsId(code);
				temp.setHouse(houseUrl);
				temp.setHouseId(houseId);
				if(houseId.size() != 0){
				houseTemp.put(id++, temp);
				}
			}
		}
//		System.err.println("before");
//		for(int i:houseTemp.keySet()){
//			TempHouseInfo temp =houseTemp.get(i);
//			System.out.println("i: "+i+"  hid:  "+temp.getHouseId());
//		}
		return houseTemp;
	}
	
	//删除桶中的hid为空的信息
		public HashMap<Integer,TempHouseInfo>  removeNull(){
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
	//以桶为单位将houseTemp转化为HouseInfo
	public HashMap<Integer,HouseInfo> getHouseInfo(){
		//统计一个桶内的所有单词并计算每个单词的出现次数
		for(int id: houseTemp.keySet()){
			TempHouseInfo tem = houseTemp.get(id);
			String code = tem.getBucketsId();
//			String sqlCode= "select *from test_pre_ganji1 where 编码= '"+code+"'";
			String sqlCode= "select *from data where 编码= '"+code+"'";
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
			LinkedList<Integer> hListId = new LinkedList<Integer>();
			//记录一个桶内部的每个词的出现次数
			HashMap<String, Integer> singleWordsNum = new HashMap<String, Integer>();
			HashMap <String, Integer> wordsNum = new HashMap <String, Integer>();
			hListId = tem.getHouseId();
			for(Integer hid: hListId){
				wordsNum = nomalTF.get(hid);
				for(String word: wordsNum.keySet()){
					if(singleWordsNum.containsKey(word)){
						singleWordsNum.put(word, singleWordsNum.get(word)+wordsNum.get(word));
					}else{
						singleWordsNum.put(word, wordsNum.get(word));
					}
				}
			}
			HouseInfo house = new HouseInfo();
			house.setId(id);
			house.setCode(code);
			house.setWords(singleWordsNum);
			house.setMatched(false);
			if(house != null){
				houseInfo.put(id, house);
		   }
	     }
		return houseInfo;
	  }
		
	//是否更新相同code桶的信息
	public int RefreshSameCode(){	
		getHouseInfo();
    	//根据计算所得的相似度和设定的阈值比较	
		for(int id1: houseInfo.keySet()){
			allRecordsNum++;
			 HouseInfo info1 = houseInfo.get(id1);
			String code1 = info1.getCode();
			for(int id2: houseInfo.keySet()){
				 HouseInfo info2 = houseInfo.get(id2);
				String code2 = info1.getCode();
				if(code1.equals(code2) && id1>id2){
					double flag = similarity(info1,info2);	
					if(flag>threshold){
//						System.out.println("similarity: "+flag);
						System.out.println("code1   "+info1.getId()+"   code2   "+info2.getId()+"  flag  "+flag);
						matchedRecordsNum++;
						info2.setMatched(true);
						if(houseTemp.containsKey(id1) && houseTemp.containsKey(id2)){
//							System.out.println("yes");
						  TempHouseInfo add = houseTemp.get(id1);
						  TempHouseInfo del = houseTemp.get(id2);
								LinkedList<Integer> delList = del.getHouseId();
								LinkedList<Integer> addList = add.getHouseId();
//								System.err.println("del  "+delList);
//								System.err.println("add  "+addList);
								add.getHouseId().addAll(delList);
								houseTemp.remove(id2);
						}
				}
		    }
		}
	}
//		System.err.println("after");
//		for(int i:houseTemp.keySet()){
//			TempHouseInfo temp =houseTemp.get(i);
//			System.out.println("i: "+i+"  hid:  "+temp.getHouseId());
//		}
		System.out.println("总的记录条数： "+allRecordsNum+"  匹配的记录条数："+matchedRecordsNum+"  匹配的比率："+matchedRecordsNum*1.0/allRecordsNum);
        return matchedRecordsNum;
	}
	
	//实现同一编码的桶之间的更新的交互过程
	public void refreshInteraction(){
		int tempNum1 = 0;
		int tempNum2 = 0;
		int num = 0;
		while(true){
			if(houseInfo.size() == 0){
				tempNum1 = RefreshSameCode();
			}
			//统计目前的已更新的个数
			for(int id: houseInfo.keySet()){
				if(houseInfo.containsKey(id)){
					HouseInfo info = houseInfo.get(id);
					if(info.isMatched()){
						num++;
					}
			  }
			}
			if(num == houseInfo.size()) break;
			if(num<houseInfo.size()){
				tempNum2 = RefreshSameCode();
			}
			if(tempNum1>=tempNum2)
				break;
			allRecordsNum = 0;
			matchedRecordsNum = 0;
		}
	}
	
	//对桶按照是否为空分类
	public void SortBucketsByNull(){
		for(String i: buckets.keySet()){
			LinkedList<House> nullHouse = new LinkedList<House>();
			LinkedList<House> noneNullHouse = new LinkedList<House>();
			if(i != "" && i.contains("null")){
				for(House h: buckets.get(i)){
					nullHouse.add(h);
				}
				nullCode.put(i, nullHouse);
			}else{
				for(House h: buckets.get(i)){
					noneNullHouse.add(h);
				}
			  noneNullCode.put(i, noneNullHouse);
			}
		}
	}
    //获取存在空的编码中与非空编码最近的一些桶
	public void getNearestBuckets(){
		for(String code: nullCode.keySet()){
			String[] nullbuc = code.split("-");
			for(String code1: noneNullCode.keySet()){
				boolean flag = true;
				String[] nonebuc = code1.split("-");
				for(int i=0; i<nullbuc.length; i++){
					if(nullbuc[i].equals(nonebuc[i]) || nullbuc[i].equals("null")){
						continue;
					}else{
						flag = false;
					}
				}
				if(flag == true){
					//在此处进行判断
					System.out.println(code+"与"+code1+"很相近");
				}
			}
		}
	}
	
	//对houseTemp分为含null和不含null的两类
	public void sortByHouseCode(){
		for(int id:houseTemp.keySet()){
			TempHouseInfo temp = houseTemp.get(id);
			if(temp.getBucketsId().contains("null")){
				houseTempNull.put(id,temp);
			}else{
				houseTempNone.put(id,temp);
			}	
		}
//		for(int id:houseTempNull.keySet()){
//			TempHouseInfo temp = houseTempNull.get(id);
//			System.out.println("houseTempNull:"+temp.getBucketsId());
//		}
//        for(int id:houseTempNone.keySet()){
//    		TempHouseInfo temp = houseTempNone.get(id);
//			System.out.println("houseTempNone:"+temp.getBucketsId());		
//		}
	}
	
	//实现存在空编码的桶之间的更新的交互过程
		public void refreshNullInteraction(){
			int num = 0;
			while(true){
				if(houseInfo.size() == 0){
					refreshNullCode();
				}
				//统计目前的已更新的个数
//				for(HouseInfo info: houseInfo){
//					if(info.isMatched()){
//						num++;
//					}
//				}
				if(num == houseInfo.size()) return;
				if(num<houseInfo.size()){
					refreshNullCode();
				}
			}
		}
		
	//计算编码存在空值时，具有最高相似性的桶之间每个urlbuckets的相似度，如果大于阈值则将其更新
	public void refreshNullCode(){
		getHouseInfo();
		for(int id1: houseTempNull.keySet()){
			TempHouseInfo tempNull = houseTempNull.get(id1);
			String[] nullInfo = tempNull.getBucketsId().split("-");
			HouseInfo info1 = houseInfo.get(id1);
			String code1 = info1.getCode();
			for(int id2: houseTempNone.keySet()){
				TempHouseInfo tempNone =houseTempNone.get(id2);
				String[] noneInfo = tempNone.getBucketsId().split("-");
				HouseInfo info2 = houseInfo.get(id2);
				String code2 = info1.getCode();
				boolean flag = true;
				for(int i=0; i<nullInfo.length; i++){
					if(nullInfo[i].equals(noneInfo[i]) || nullInfo[i].equals("null")){
						continue;
					}else{
						flag = false;
					}
				}
				if(flag == true){
					//在此处进行判断
					//System.err.println("nullid: "+tempNull.getId()+"  noneid: "+tempNone.getId());
					//System.out.println(tempNull.getBucketsId()+"与"+tempNone.getBucketsId()+"很相近");
					//System.out.println("r1 "+info1.getWords()+" r2 "+info2.getWords());
					//System.err.println("code1: "+code1+" tempNull: "+ tempNull.getBucketsId()+" code2: "+code2+" tempNone: "+tempNone.getBucketsId());
					if(code1.equals(tempNull.getBucketsId())  && id1!=id2){
						double flag1 = similarityNull(info1,info2);	
						if(flag1>threshold){
							System.out.println("code1   "+info1.getId()+"   code2   "+info2.getId()+"  flag1  "+flag1);
							info2.setMatched(true);
							if(houseTemp.containsKey(id1) && houseTemp.containsKey(id2)){
								System.out.println("yes");
							    TempHouseInfo del = houseTemp.get(id1);
							    TempHouseInfo add = houseTemp.get(id2);
								LinkedList<Integer> delList = del.getHouseId();
								LinkedList<Integer> addList = add.getHouseId();
								System.err.println("del  "+delList);
								System.err.println("add  "+addList);
								add.getHouseId().addAll(delList);
								houseTemp.remove(id1);
							   }
					       }
			         }
				}
			}
		}
//		System.err.println("after");
//		for(int i:houseTemp.keySet()){
//			TempHouseInfo temp =houseTemp.get(i);
//			System.out.println("i: "+i+"  hid:  "+temp.getHouseId());
//		}
	}
    //获取cosine向量，计算相似度
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
				//System.out.println("singleWordsNum1 "+singleWordsNum1+" singleWordsNum2 "+singleWordsNum2);
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
	
	//获取cosine向量，计算相似度
		public double similarityNull(HouseInfo info1, HouseInfo info2){
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
					singleWordsNum1 = info1.getWords();
					singleWordsNum2 = info2.getWords();
					//System.out.println("singleWordsNum1 "+singleWordsNum1+" singleWordsNum2 "+singleWordsNum2);
					for(String word1: singleWordsNum1.keySet()){
						wordSet.add(word1);
					}
					for(String word2: singleWordsNum1.keySet()){
						wordSet.add(word2);
					}
					for(String w1: wordSet)
					{
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
			    	for(int k = 0; k<vector1.size(); k++){
			    		numcrator += vector1.get(k)*vector2.get(k);
			    		temp1 += Math.pow(vector1.get(k), 2.0);
			    		temp2 += Math.pow(vector2.get(k), 2.0);
			    	}
			    	denominator = Math.sqrt(temp1)*Math.sqrt(temp2);
			    	similarity = numcrator/denominator;	
			return similarity;
		}
		
	public static void main(String[]args){
		//String sql = "select * from test_pre_ganji1 group by 编码";
		//String sql = "select * from test_pre_ganji1";
		BucketsInMatch1 bim = new BucketsInMatch1();
		bim.getBuckets();
		bim.getSmallBuckets();
		//System.out.println(bim.getBuckets().size());
		//bim.SortBucketsByNull();
		//bim.getNearestBuckets();
		//System.out.println(bim.getSmallBuckets());
		bim.getTempInfo();
		bim.sortByHouseCode();
		//bim.refreshNullCode();
		bim.RefreshSameCode();
		//bim.refreshInteraction();
		//bim.SortBucketsByNull();
		//bim.refreshNullCode();
		//bim.freshInBuckets();
		//bim.getNullAndNoneBuckets(sql);
		//System.out.println(bim.getSmallBuckets(bim.getBuckets(sql, sql1)));
		//System.out.println(bim.dataTwoHouse(sql).size());
	}
}