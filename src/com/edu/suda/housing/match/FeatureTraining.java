package com.edu.suda.housing.match;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import com.edu.suda.housing.feature.TextPreprocess;
import com.edu.suda.housing.graph.MyAdjGraphic;
import com.edu.suda.housing.graph.Weight;

import database.DBconnection;
import file.MyTime;

public class FeatureTraining {

	public DBconnection db = DBconnection.getDbConnection();
	public static String sql = "select * from data";
	//存放临时聚类结果  groundtruth1  test_pre_ganji1
	public HashMap<String, HashMap<String, Integer>> tempcluster = new HashMap<String, HashMap<String,Integer>>();
	public HashMap<String, HashMap<String, Integer>> clusters = new HashMap<String, HashMap<String,Integer>>();
	//存放最终聚类结果
	public HashMap<Integer,MyAdjGraphic> cluster = new HashMap<Integer,MyAdjGraphic>();
	
	public ArrayList<String> vertices = new ArrayList<String>();
	public ArrayList<Weight> weights = new ArrayList<Weight>();
	public int vecNum = 0;
	
	/**
	 * 聚类过程
	 * @return 
	 * */
    public HashMap<String, HashMap<String, Integer>> getFeatrueCluster(){
    	try{
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				String des = rs.getString("房源描述");
				LinkedList<ArrayList<String>> tem = new LinkedList<ArrayList<String>>();
				tem = TextPreprocess.getFeatureSet(TextPreprocess.getSubSentence(des));
				for(int i = 0;i<tem.size();i++){
					ArrayList<String> arr = new ArrayList<String>();
					 arr = tem.get(i);	
						if(!arr.isEmpty()){														
							for(int j=0;j<arr.size();j++){
//								HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
								if(tempcluster.containsKey(arr.get(j))){
									//tempMap = tempcluster.get(arr.get(j));
										for(int k=0;k<arr.size();k++){
											if(!arr.get(j).equals(arr.get(k))){
												if(tempcluster.get(arr.get(j)).containsKey(arr.get(k))){
													tempcluster.get(arr.get(j)).put(arr.get(k), tempcluster.get(arr.get(j)).get(arr.get(k))+1);
												}else{
													tempcluster.get(arr.get(j)).put(arr.get(k), 1);
												}
											}
										}
								}else{
									tempcluster.put(arr.get(j), new HashMap<String,Integer>());
									for(int k=0; k<arr.size();k++){
										if(!arr.get(j).equals(arr.get(k))){
											tempcluster.get(arr.get(j)).put(arr.get(k), 1);
										}
									}
								}
							}							
						  }
					 }
			    }
				//System.out.println("\n"+"================");
			rs.close();			
		}catch(Exception e){
			System.out.println("database read exception"+e.toString());
		}
//    	System.out.println("AllSize: "+tempcluster.size()+"tempcluster"+tempcluster);
    	return tempcluster;
    }

    /**
     * 融合所有的临时聚类得到最终的聚类结果
     * @return 
     * */
    public HashMap<Integer, MyAdjGraphic> mergeTempCluster() throws Exception{
    	getFeatrueCluster();
    	HashMap<Integer, HashMap<String, HashMap<String, Integer>>> classMap = new HashMap<Integer, HashMap<String,HashMap<String,Integer>>>();
    	classMap = getElement(tempcluster);
    	System.out.println(classMap);
    	for(Integer clusterId: classMap.keySet()){
    		clusters = classMap.get(clusterId);
    		for(String keyWord: clusters.keySet()){
    			if(!vertices.contains(keyWord)){
    				vertices.add(keyWord);
    			}       		
        		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
        		tempMap = clusters.get(keyWord);
        		for(String inKeyWord: tempMap.keySet()){
        			if(inKeyWord!= null && inKeyWord!= ""){
        				 if(!vertices.contains(inKeyWord)){
        					  vecNum ++;
        				      vertices.add(inKeyWord);
        				      Weight w = new Weight(vertices.indexOf(keyWord),vertices.indexOf(inKeyWord),tempMap.get(inKeyWord));
        				      weights.add(w);
        				  }else{
        					  vecNum ++;
        				      Weight w = new Weight(vertices.indexOf(keyWord),vertices.indexOf(inKeyWord),tempMap.get(inKeyWord));
        				      weights.add(w);
        				  }			 
        			
        		  }
        		}
    		}
    		int n = vertices.size(); //顶点个数
            int e = vecNum; //边个数
            MyAdjGraphic g = new MyAdjGraphic(n);
            String[] vertice = (String[]) vertices.toArray(new String [vertices.size()]);
            Weight[] weight = (Weight[]) weights.toArray(new Weight [weights.size()]);
      	    buildAdjGraphic(g, vertice,n,weight,vecNum);
      	    if(e>1){
//      	      System.out.println("边个数： "+e+"  clusterId:"+clusterId);
  			      cluster.put(clusterId, g);
//	  			  System.out.println("第"+(clusterId)+"个聚类的信息如下：");
//	  		      System.out.println("--------该邻接矩阵如下---------");
//		          g.print();
//		          System.out.println("结点的个数："+g.getNumOfVertice()+"  所有的节点："+g.getValueOfVertice());
//		          System.out.println("边的个数："+g.getNumOfEdges());
//		          System.out.println("第"+(clusterId++)+"个聚类的信息结束\n==================================");
  		    }
  		      
	          //恢复初始状态
	          vertices.clear();
  		      weights.clear();
  		      vecNum = 0;
    	}
    	return cluster;
}
       
    /**
     * 获取与该key相接的下一个Map
     * 
     * */
    @SuppressWarnings("null")
	public void getNext(String key, HashMap<String, Integer> map){
      if(map!=null || !map.isEmpty()){
		for(String inKeyWord: map.keySet()){
			if(inKeyWord!= null| inKeyWord!= ""){
				 if(!vertices.contains(inKeyWord)){
					 vecNum ++;
				      vertices.add(inKeyWord);
				      Weight w = new Weight(vertices.indexOf(key),vertices.indexOf(inKeyWord),map.get(inKeyWord));
				      weights.add(w);
				  }			 
			if(tempcluster.containsKey(inKeyWord)){	
				getNext(inKeyWord,tempcluster.get(inKeyWord));
			}else{
				continue;
			}
		  }
		}
      }else{
    	  return;
      } 
	}
    
    /**
     * 获取与该key相接的下一个Map
     * 
     * */
	public HashMap<Integer, HashMap<String, HashMap<String, Integer>>> getElement(HashMap<String, HashMap<String, Integer>> tempcluster){
    	PriorityQueue<String> U=new PriorityQueue<String>();
    	U.addAll(tempcluster.keySet());
//    	System.out.println("tempcluster: "+tempcluster);
//    	System.out.println("U"+U);
    	int currentClassID=1;
    	PriorityQueue<String> candidatasMap=new PriorityQueue<String>();
    	HashSet<String> VisitedMap=new HashSet<String>();
    	HashMap<Integer, HashMap<String, HashMap<String, Integer>>> classMap=new HashMap<Integer, HashMap<String,HashMap<String,Integer>>>();
    	
    	//U 表示所有字段
    	String currentKWD=U.poll();  
    	candidatasMap.add(currentKWD);
    	classMap.put(currentClassID, new HashMap<String, HashMap<String, Integer>>());
    	while(candidatasMap.size()>0)
    	{
//    		 System.out.println("candidatasMap: "+candidatasMap);
    		 currentKWD=candidatasMap.poll();
//    		 System.out.println("currentKWD1: "+currentKWD);
    		 //判断当前节点是否已经访问
    		 if(VisitedMap.contains(currentKWD))
    		 {
    			 continue;
    		 }
    		 //判断currentClassID中是否存在currentKWD节点
    		 if(!classMap.get(currentClassID).containsKey(currentKWD))
    		 {
    			 classMap.get(currentClassID).put(currentKWD,new HashMap<String, Integer>());
    		 }
    		 //统计currentKWD与tmpSeed之间的权值
    		 for(String tmpSeed:tempcluster.get(currentKWD).keySet())
    		 {
//    			 System.out.println("tmpSeed: "+tmpSeed);
    			 if(classMap.get(currentClassID).get(currentKWD).containsKey(tmpSeed))
    			 {
    				 classMap.get(currentClassID).get(currentKWD).put(tmpSeed, classMap.get(currentClassID).get(currentKWD).get(tmpSeed));	 
    			 }else
    			 {
    				 classMap.get(currentClassID).get(currentKWD).put(tmpSeed, tempcluster.get(currentKWD).get(tmpSeed));
    			 }
    			 //构建待访问的队列
    			 if(!VisitedMap.contains(tmpSeed))
    			 {
    				 candidatasMap.add(tmpSeed);
    			 }
    		 }
    		 //将currentKWD加入已访问列表
    		 VisitedMap.add(currentKWD);
//    		 System.out.println("candidatasMap.size(): "+candidatasMap.size());
    		 if(candidatasMap.size()==0)
    		 {
    			 if(U.size()>0)
    			 {
    				 currentKWD=U.poll();
    				 while(VisitedMap.contains(currentKWD))
    				 {	if((U.size()>0))
    				 	{
    					 currentKWD=U.poll();
    				 	}else
    				 	{
    				 		break;
    				 	}
    					
    				 }
//    				 System.err.println("currentKWD"+currentKWD);
    				 if(currentKWD!=null){
    				   candidatasMap.add(currentKWD);
    				   classMap.put(currentClassID+1, new HashMap<String, HashMap<String, Integer>>());
  	    			   currentClassID=classMap.size();
    				 }
    			 }
    		 }
    	}
//    	System.out.println("classMap:  "+ classMap);
       return classMap;
	}
    
    /**
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
		FeatureTraining ft = new FeatureTraining();
		long before = System.currentTimeMillis();
//		System.out.println(ft.getFeatrueCluster());
//		System.out.println(ft.mergeTempCluster());
		long current = System.currentTimeMillis();
		MyTime.gettime(current-before);
	}

}
