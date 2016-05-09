package com.edu.suda.housing.match;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

import test.EditDistanceSimilarityFunction;

import database.DBconnection;
import house.House;

import com.edu.suda.housing.match.CopyOfBucketsInMatch.TempHouseInfo;
import com.edu.suda.housing.tfidf.*;
import com.edu.suda.housing.tools.PrintData;
public class CopyOfBucketsInMatch {
	
	public DBconnection db = DBconnection.getDbConnection();
	public LinkedList <House> houseList = new LinkedList<House>();
	public LinkedList <House> innerHouse = new LinkedList<House>();
	
	public Map<String, LinkedList<House>> buckets = new HashMap<String, LinkedList<House>>();
	public LinkedList<TempHouseInfo> houseTemp = new LinkedList<TempHouseInfo>();
	public LinkedList<HouseInfo> houseInfo = new LinkedList<HouseInfo>();
	//public LinkedList<HashMap<String, LinkedList<House>>> newBUckets = new LinkedList<HashMap<String, LinkedList<House>>>();
	public HashMap<String, LinkedList<House>> urlbuckets = new HashMap<String, LinkedList<House>>();
	
	public Map<String, LinkedList<House>> noneNullCode = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> nullCode = new HashMap<String, LinkedList<House>>();
	
	public LinkedList<TempHouseInfo> houseTempNull = new LinkedList<TempHouseInfo>();
	public LinkedList<TempHouseInfo> houseTempNone = new LinkedList<TempHouseInfo>();
	
	EditDistanceSimilarityFunction sim = new EditDistanceSimilarityFunction();	
 
	CountIFIDF cii = new CountIFIDF();
	
//	public LinkedList<Integer> add = new LinkedList<Integer>();
//	public LinkedList<Integer> del = new LinkedList<Integer>();
	
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
		//System.out.println(buckets);
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
	  //System.out.println("urlbuckets"+urlbuckets);
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
	public LinkedList<TempHouseInfo> getTempInfo(){
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
				temp.setId(id++);
				temp.setBucketsId(code);
				temp.setHouse(houseUrl);
				temp.setHouseId(houseId);
				if(houseId.size() != 0){
				houseTemp.add(temp);
				}
			}
		}
		System.err.println("before");
		for(TempHouseInfo tem: houseTemp){
			System.out.println("id  "+ tem.getId()+"  bid=========:"+tem.getBucketsId()+"  hid======:"+tem.getHouseId());
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
//					if(isRefreshBuckets(l1,l2)){
//						l1.addAll(add);
//						l2.removeAll(del);						
//						System.out.println("当前的桶更新了"+(times++));
//					}
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
	public LinkedList<HouseInfo> getHouseInfo(){
		//统计一个桶内的所有单词并计算每个单词的出现次数
		for(TempHouseInfo tem: houseTemp){
			String code = tem.getBucketsId();
			int id = tem.getId();
			String sqlCode= "select *from test_pre_ganji1 where 编码= '"+code+"'";
			
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
				houseInfo.add(house);
		   }
	     }
		//System.out.println(houseInfo.size());
//		for(HouseInfo tem: houseInfo){
//			System.out.println("编号"+tem.getId()+"===========bid"+tem.getCode()+" ===========hid"+tem.getWords());
//			//PrintData.print(tem.getHouse());
//		}
		return houseInfo;
	  }
	
	//是否更新相同code桶的信息
	public void RefreshSameCode(){				
		getHouseInfo();
    	//根据计算所得的相似度和设定的阈值比较	
		for(int i = 0; i<houseInfo.size(); i++){
			HouseInfo info1 = houseInfo.get(i);
			String code1 = info1.getCode();
			for(int j = 0; j<houseInfo.size(); j++){
				HouseInfo info2 = houseInfo.get(j);
				String code2 = info2.getCode();
				if(code1.equals(code2) && i!=j){
					double flag = similarity(info1,info2);
					System.out.println("code1   "+code1+"   code2   "+code2+"  flag  "+flag);
					if(flag>threshold){
						int id1 = info1.getId();
						int id2 = info2.getId();
						info2.setMatched(true);
						for(Iterator<TempHouseInfo> it = houseTemp.iterator(); it.hasNext();){
							TempHouseInfo temp = (TempHouseInfo) it.next();
							if(temp.getId() == id1){
                              
						  }else if(temp.getId() == id2){
							
						}else{
							continue;
						}
					  }
					}
				}
			}
		}
		System.err.println("after");
		for(TempHouseInfo tem: houseTemp){
			System.out.println("bid=========:"+tem.getBucketsId()+"  hid======:"+tem.getHouseId());
			//PrintData.print(tem.getHouse());
		}
    }
	
	//实现同一编码的桶之间的更新的交互过程
	public void refreshInteraction(){
		int num = 0;
		while(true){
			if(houseInfo.size() == 0){
				RefreshSameCode();
			}
			//统计目前的已更新的个数
			for(HouseInfo info: houseInfo){
				if(info.isMatched()){
					num++;
				}
			}
			if(num == houseInfo.size()) return;
			if(num<houseInfo.size()){
				RefreshSameCode();
			}
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
		for(TempHouseInfo temp: houseTemp){
			if(temp.getBucketsId().contains("null")){
				houseTempNull.add(temp);
			}else{
				houseTempNone.add(temp);
			}		
		}
//		System.out.println("houseTempNull:"+houseTempNull);
//		System.out.println("houseTempNone:"+houseTempNone);
	}
	
	//实现存在空编码的桶之间的更新的交互过程
		public void refreshNullInteraction(){
			int num = 0;
			while(true){
				if(houseInfo.size() == 0){
					refreshNullCode();
				}
				//统计目前的已更新的个数
				for(HouseInfo info: houseInfo){
					if(info.isMatched()){
						num++;
					}
				}
				if(num == houseInfo.size()) return;
				if(num<houseInfo.size()){
					refreshNullCode();
				}
			}
		}
		
	//计算编码存在空值时，具有最高相似性的桶之间每个urlbuckets的相似度，如果大于阈值则将其更新
	public void refreshNullCode(){
		for(TempHouseInfo tempNull: houseTempNull){
			String[] nullInfo = tempNull.getBucketsId().split("-");
			for(TempHouseInfo tempNone: houseTempNone){
				String[] noneInfo = tempNone.getBucketsId().split("-");
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
					//System.out.println(tempNull.getBucketsId()+"与"+tempNone.getBucketsId()+"很相近");
					getHouseInfo();
					for(int i = 0; i<houseInfo.size(); i++){
						HouseInfo info1 = houseInfo.get(i);
						String code1 = info1.getCode();
						for(int j = 0; j<houseInfo.size(); j++){
							HouseInfo info2 = houseInfo.get(j);
							String code2 = info2.getCode();
							if(i!=j){
								double flag1 = similarity(info1,info2);
								if(flag1>threshold){
									int id1 = info1.getId();
									int id2 = info2.getId();
									info2.setMatched(true);
									ListIterator<TempHouseInfo> lt1 = houseTemp.listIterator();
									ListIterator<TempHouseInfo> lt2 = houseTemp.listIterator();
									while(lt1.hasNext()){
									  while(lt2.hasNext()){
										TempHouseInfo temp1 = lt1.next();
										TempHouseInfo temp2 = lt2.next();
										if(temp2.getId() == id2 && temp1.getId() == id1 && temp2.getId()!= temp1.getId()){
											temp1.getHouseId().addAll(temp2.getHouseId());
											lt2.remove();
										}
									}
								  }
								}
							}
						}
					}
				}
			}
		}
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
			}
		return similarity;
	}
	public static void main(String[]args){
		//String sql = "select * from test_pre_ganji1 group by 编码";
		//String sql = "select * from test_pre_ganji1";
		CopyOfBucketsInMatch bim = new CopyOfBucketsInMatch();
		System.out.println(bim.getBuckets().size());
		//bim.SortBucketsByNull();
		//bim.getNearestBuckets();
		System.out.println(bim.getSmallBuckets());
		bim.getTempInfo();
		bim.sortByHouseCode();
		//bim.refreshNullCode();
		bim.RefreshSameCode();
		//bim.refreshInteraction();
		//bim.freshInBuckets();
		//bim.getNullAndNoneBuckets(sql);
		//System.out.println(bim.getSmallBuckets(bim.getBuckets(sql, sql1)));
		//System.out.println(bim.dataTwoHouse(sql).size());
	}
}