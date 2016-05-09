package com.edu.suda.housing.match;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.edu.suda.housing.feature.TextPreprocess;
import com.edu.suda.housing.graph.MyAdjGraphic;
import com.edu.suda.housing.graph.Weight;

import database.DBconnection;

public class FeatureTraining1 {

	private static final double Threshold = 0.5;
	public DBconnection db = DBconnection.getDbConnection();
	public static String sql = "select * from groundtruth1";
	//存放临时聚类结果
	public HashMap<String, HashMap<String, Integer>> tempcluster = new HashMap<String, HashMap<String,Integer>>();
	//存放最终聚类结果
	public HashMap<String, HashMap<String, Integer>> cluster = new HashMap<String, HashMap<String,Integer>>();
	
	public ArrayList<String> vertices = new ArrayList<String>();
	public ArrayList<Weight> weights = new ArrayList<Weight>();
	public int vecNum = 0;
	
	//聚类过程
    public void getFeatrueCluster(){
    	try{
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				String des = rs.getString("房源描述");
				LinkedList<ArrayList<String>> tem = new LinkedList<ArrayList<String>>();
				tem = TextPreprocess.getFeatureSet(TextPreprocess.getSubSentence(des));
				for(int i = 0;i<tem.size();i++){
					ArrayList<String> arr = new ArrayList<String>();
					arr = tem.get(i);
					//System.out.print(arr+" ");
				  if(arr.get(0)!="" ||arr.get(0)!=null){
					HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
					for(int j=1;j<arr.size();j++){
						if(tempcluster.containsKey(arr.get(0))){
							tempMap = tempcluster.get(arr.get(0));
							if(tempMap.containsKey(arr.get(j))){
								tempMap.put(arr.get(j), tempMap.get(arr.get(j))+1);
							}else{
								tempMap.put(arr.get(j), 1);
							}
						}else{
							tempMap.put(arr.get(j), 1);						
						}
					}
					tempcluster.put(arr.get(0), tempMap);
				  }else{
					  continue;
				  }
				}
				//System.out.println("\n"+"================");
		  }
			rs.close();			
		}catch(Exception e){
			System.out.println("database read exception"+e.toString());
		}
    }
    /*
     * 融合所有的临时聚类得到最终的聚类结果
     * */
    
//    public void mergeTempCluster(){
//    	for(String keyWord: tempcluster.keySet()){
//    		ArrayList<String> vertices = new ArrayList<String>();
//    		vertices.add(keyWord);
//    		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
//    		tempMap = tempcluster.get(keyWord);
//    		for(String inKeyWord: tempMap.keySet()){
//    			vertices.add(inKeyWord);
//    			if(tempcluster.containsKey(inKeyWord)){
//    				HashMap<String, Integer> tempMap1 = new HashMap<String, Integer>();
//    				tempMap1 = tempcluster.get(inKeyWord);
//    				for(String Key: tempMap.keySet()){
//    					vertices.add(inKeyWord);
//    					if(tempcluster.containsKey(Key)){
//    	    				HashMap<String, Integer> tempMap2 = new HashMap<String, Integer>();
//    	    				tempMap2 = tempcluster.get(Key);
//    	    				for(String Key1: tempMap.keySet()){
//    	    					vertices.add(inKeyWord);
////    	    					...
//    	    				}
//    				}
//    			}
//    		}
//    	}
//    }
// }
    /*
     * 融合所有的临时聚类得到最终的聚类结果
     * */
    public void mergeTempCluster() throws Exception{
    	for(String keyWord: tempcluster.keySet()){
    		vertices.add(keyWord);
    		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
    		tempMap = tempcluster.get(keyWord);
    		getNext(keyWord,tempMap);
    		int n = vertices.size(); //顶点个数
            int e = vecNum; //边个数
            MyAdjGraphic g = new MyAdjGraphic(n);
            String[] vertice = (String[]) vertices.toArray();
            Weight[] weight = (Weight[]) weights.toArray();
    		buildAdjGraphic(g, vertice,n,weight,vecNum);
    		System.out.println("--------该临街矩阵如下---------");
            g.print();
            System.out.println("结点的个数："+g.getNumOfVertice());
            System.out.println("边的个数："+g.getNumOfEdges());
    		vertices.clear();
    		weights.clear();
    		vecNum = 0;
    }
}
    
//    public HashMap<String, Integer> getNext(String key){
//    	HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
//		tempMap = tempcluster.get(key);
//		if(tempMap==null) return null;
//		for(String inKeyWord: tempMap.keySet()){
//			//vertices.add(inKeyWord);
//			if(tempcluster.containsKey(inKeyWord)){				
//				getNext(inKeyWord);
//			}
//		}
//		return tempMap;
//	}
    
    /*
     * 获取与该key相接的下一个Map
     * 
     * */
    public void getNext(String key, HashMap<String, Integer> map){
      if(map!=null){
		for(String inKeyWord: map.keySet()){
			if(inKeyWord!= null| inKeyWord!= ""){
				 if(!vertices.contains(inKeyWord)){
					 vecNum++;
				      vertices.add(inKeyWord);
				      Weight w = new Weight(vertices.indexOf(key),vertices.indexOf(inKeyWord),map.get(inKeyWord));
				      weights.add(w);
				  }
			 }
			if(tempcluster.containsKey(inKeyWord)){	
				getNext(inKeyWord,tempcluster.get(inKeyWord));
			}else{
				continue;
			}
		}
      }else{
    	  return;
      } 
	}
    /*
     * 构建邻接矩阵
     * */
    public void buildAdjGraphic(MyAdjGraphic g, String[] vertices, int n,Weight[] weight,int e) throws Exception{
    	Weight.createAdjGraphic(g, vertices, n, weight, e);
    }
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FeatureTraining1 ft = new FeatureTraining1();
		ft.getFeatrueCluster();
		ft.mergeTempCluster();
	}

}
