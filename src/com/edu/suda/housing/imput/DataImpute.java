package com.edu.suda.housing.imput;

import house.House;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import com.edu.suda.housing.tfidf.CountIFIDF;
import com.edu.suda.housing.tools.DFIndex;
import com.edu.suda.housing.tools.IndexDic;

import database.DBconnection;

public class DataImpute {

	public Map<String, LinkedList<House>> buckets = new HashMap<String, LinkedList<House>>();
	public DBconnection db = DBconnection.getDbConnection();
	public static String sql = "select * from test_pre_ganji1";
	public Map<String, LinkedList<House>> noneNullCode = new HashMap<String, LinkedList<House>>();
	public Map<String, LinkedList<House>> nullCode = new HashMap<String, LinkedList<House>>();
	
	public Map<Integer, String> modefyHouseInfo = new HashMap<Integer, String>();
	public Map<String, String> dfImpute = new HashMap<String, String>();
	
	CountIFIDF cii = new CountIFIDF();
	
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
	
	//对编码中存在空值的处理
		/*
		 * 1、根据描述信息进行填充
		 * 2.根据DF进行填充（小区-》位置）
		 * */	
		
		/*
		 * 将原先的桶根据编码是否有空值分为两大类：含空和不含空
		 * 
		 * */
		public void getNullAndNoneBuckets(){
			for(String i: buckets.keySet()){
				LinkedList<House> nullHouse = new LinkedList<House>();
				LinkedList<House> noneNullHouse = new LinkedList<House>();			
				 for(House house : buckets.get(i)){
					 if(house.code.contains("null")){
						 nullHouse.add(house);
						 //System.out.println("yes");
					 }else if(!house.code.contains("null")){
						 noneNullHouse.add(house);
						 //System.out.println("no");
					 }
				 }
				 if(noneNullHouse!=null){
				 noneNullCode.put(i, noneNullHouse);
				 }
				 if(nullHouse != null){
				 nullCode.put(i, nullHouse);
				 }
			}
			System.out.println("bcukets"+buckets);
			System.out.println("noneNullCcoe"+noneNullCode+"  nullCode"+nullCode);
		}
		public void createDFDic(){
			
		}
		/*
		 * 使用DF实现空缺值的填充
		 * */
		public void imputByDF(String table){
			dfImpute = DFIndex.getMap();
			 try{
				   ResultSet rs = db.executeQuery(sql);			       
			    	 while(rs.next()){   
			    		 for(String xiaoqu: dfImpute.keySet()){
			    			 System.err.println(rs.getString("小区").trim().equals(xiaoqu));
					   if(rs.getString("位置")==""||rs.getString("位置") ==null|| !rs.getString("位置").equals(dfImpute.get(xiaoqu))){
						   if(rs.getString("小区").trim().equals(xiaoqu)){
							   int i = db.executeUpdate("update "+table+" set 位置  = "+dfImpute.get(xiaoqu));
						   }
					   }
					  }
			      }
			     rs.close();
			   }catch(Exception e){
				    System.err.println("数据读取异常！"+e.toString());
			   }
		}
		
		/*
		 * 使用小区、面积，，，的编码所得的dictionary，结合描述信息的结果，找出对应的缺失信息
		只有一个null的case
		*/
		@SuppressWarnings("unchecked")
		public void imputNullCodeByDes(String tableName, String col){
			//String sql1 = "select *from "+tableName;
			Map<Integer, HashMap<String, Float>>  tfidf = cii.getTFIDF(sql);
			HashSet<IndexDic> dic = new HashSet<IndexDic>();
			dic = IndexDic.indexDic(tableName, col);
			//System.out.println("dic"+dic);
			for(String i: buckets.keySet()){
				if(buckets.get(i).get(0).code.contains("null")){
					//System.out.println("code"+buckets.get(i).get(0).code);
					 for(House h: buckets.get(i)){
						 //System.out.println("before"+h.code);
						 HashMap<String, Float> words = tfidf.get(h.id);
						 //System.out.println("words"+words);
						 for(String word : words.keySet()){
							 if(dic.contains(word)){
								 System.out.println("含有待更新的内容"+word);
								 //h.code.replace("null", word);
								 h.setCode(h.code.replace("null", word));
								 modefyHouseInfo.put(h.id, h.code);
								 //System.out.println("after"+h.code);
								 break;
							 }else{
								 continue;
							 }
						 }
					 }
				   }

				}
			System.out.println("modefyHouseInfo"+modefyHouseInfo);
		   }
		
		/*
		 * 将需要更新的信息写入数据库
		 * 
		 * */
		public void writeInDatabase(String table,String col){
			try{
				ResultSet rs = db.executeQuery(sql);
				while(rs.next()){
					for(Integer id: modefyHouseInfo.keySet()){
					if(rs.getInt("id") == id){
						db.executeUpdate("update "+table+" set "+col+" = "+modefyHouseInfo.get(id)+" where id = "+id);
					 }
					}
				  }
				rs.close();
			}catch(Exception e){
				System.out.println("database read exception"+e.toString());
			}
		}
		public static void main(String[] args) {
			DataImpute impute = new DataImpute();
			impute.getBuckets();
			//impute.imputNullCodeByDes("areaindex", "area");
			//impute.writeInDatabase("test_pre_ganji1","编码");
			impute.imputByDF("test_pre_ganji1");
		}
}
