package com.edu.suda.housing.match;

import java.sql.ResultSet;
import java.util.*;

import com.edu.suda.housing.feature.TextPreprocess;
import com.edu.suda.housing.tools.ConsineImproved;

import database.DBconnection;
import file.MyTime;

public class FeatureBasedInTraining {

	private static final double Threshold = 0.5;
	public DBconnection db = DBconnection.getDbConnection();
	public static String sql = "select * from groundtruth1";
	public HashMap<Integer, ArrayList<String>> cluster = new HashMap<Integer, ArrayList<String>>();
	public HashMap<Integer, ArrayList<String>> temp = new HashMap<Integer, ArrayList<String>>();
	public HashMap<Integer, ArrayList<String>> temp1 = new HashMap<Integer, ArrayList<String>>();
	
	public void countInCluster(){
		try{
			ResultSet rs = db.executeQuery(sql);
			int clusterId = 0;
			while(rs.next()){
				String des = rs.getString("房源描述");
				LinkedList<ArrayList<String>> tem = new LinkedList<ArrayList<String>>();
				tem = TextPreprocess.getFeatureSet(TextPreprocess.getSubSentence(des));
				System.out.println("size: "+cluster.size());
				if(cluster.size()!=0){
//					System.out.println("y");
					for(int id: cluster.keySet()){
						ArrayList<String> str = cluster.get(id);
						for(int j=0; j<tem.size(); j++){
							ArrayList<String> s = tem.get(j);
							if(similarityArray(str,s)>Threshold){
								//if(!s.equals(str)){
								  str.addAll(s);
								//}
							}else if(!cluster.containsValue(s)){
								temp.put(clusterId++, s);
							}
						}
					}
					cluster.putAll(temp);
				}else{
//					System.err.println("n");
					for(int i=0; i<tem.size(); i++){
						if(!cluster.containsKey(clusterId)){
                            cluster.put(clusterId++, tem.get(i));
					}
				}
			}
		  }
			rs.close();
			for(int id: cluster.keySet()){				
				temp1.put(id, removeDuplicateWithOrder(cluster.get(id)));
			}
			cluster.putAll(temp1);
			cluster = removeSameConten(cluster);
			System.out.println("cluster: "+cluster);
		}catch(Exception e){
			System.out.println("database read exception"+e.toString());
		}
	}
	/**
     *对Map中重复值进行处理（1、ArrayList的内容相同的 2、id不同但arraylist相同的）
    */
    public  ArrayList<String> removeDuplicateWithOrder(ArrayList<String> list) {  
    	Set<String> set = new HashSet<String>();  
         List<String> newList = new ArrayList<String>();  
	       for (Iterator<String> iter = list.iterator(); iter.hasNext();) {  
	    	   String element = (String) iter.next();  
	             if (set.add(element)){  
	                newList.add(element);
	                }  
	          }   
         list.clear();  
         list.addAll(newList);
         return list;
   } 
    public HashMap<Integer, ArrayList<String>> removeSameConten(HashMap<Integer, ArrayList<String>> map){
    	HashMap<Integer, ArrayList<String>> deleteMap = new HashMap<Integer, ArrayList<String>>();
    	for(int id1: map.keySet()){
    		ArrayList<String> arr1 = map.get(id1);
    		for(int id2: map.keySet()){
    		    ArrayList<String> arr2 = map.get(id2);
    		    if(id1!= id2 && id1 < id2){
	    		    if(arr1.equals(arr2)){
	    		    	//System.err.println(arr1+"   "+arr2);
	    		    	//System.out.println("y");
	    		        deleteMap.put(id2, arr2);
	    		    }
    		    }
    		}
    	}
    	//System.out.println("size1: "+map.size()+"  size2:"+deleteMap.size());
    	//map.putAll(deleteMap);
    	//System.out.println(deleteMap);
    	for(int delId: deleteMap.keySet()){
        	map.remove(delId);
    	}
    	return map;
    }
	/**
	 * 计算两个数组之间的相似度
     */
	public double similarityArray(ArrayList<String> a,ArrayList<String> b){	
		return ConsineImproved.getSimilarity(a, b);
	}
	public static void main(String args[]){
		FeatureBasedInTraining featrue = new FeatureBasedInTraining();
		long before = System.currentTimeMillis();
		featrue.countInCluster();
		long current = System.currentTimeMillis();
		MyTime.gettime(current-before);
//		ArrayList a = new ArrayList();
//		ArrayList b = new ArrayList();
//		a.add("12");
//		a.add("23");
//		b.add("12");
//		b.add("23");
//		System.out.println(a.equals(b));
	}
}
