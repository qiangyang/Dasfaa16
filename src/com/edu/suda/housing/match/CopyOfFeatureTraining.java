package com.edu.suda.housing.match;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.edu.suda.housing.feature.TextPreprocess;
import com.edu.suda.housing.graph.MyAdjGraphic;
import com.edu.suda.housing.graph.Weight;

import database.DBconnection;
import file.MyTime;

public class CopyOfFeatureTraining {

	public DBconnection db = DBconnection.getDbConnection();
	public static String sql = "select * from groundtruth1";
	//存放临时聚类结果  groundtruth1  test_pre_ganji1
	public HashMap<String, HashMap<String, Integer>> tempcluster = new HashMap<String, HashMap<String,Integer>>();
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
//    	System.out.println("tempcluster"+tempcluster);
    	return tempcluster;
    }

    /**
     * 融合所有的临时聚类得到最终的聚类结果
     * @return 
     * */
    public HashMap<Integer, MyAdjGraphic> mergeTempCluster() throws Exception{
    	int clusterNum = 1;
    	getFeatrueCluster();
    	for(String keyWord: tempcluster.keySet()){
    		vertices.add(keyWord);
    		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
    		tempMap = tempcluster.get(keyWord);
    		getNext(keyWord,tempMap);
    		int n = vertices.size(); //顶点个数
            int e = vecNum; //边个数
            MyAdjGraphic g = new MyAdjGraphic(n);
            String[] vertice = (String[]) vertices.toArray(new String [vertices.size()]);
            Weight[] weight = (Weight[]) weights.toArray(new Weight [weights.size()]);
    		buildAdjGraphic(g, vertice,n,weight,vecNum);
    		if(e>1){
    			cluster.put(clusterNum, g);
    		}
    		System.out.println("第"+(clusterNum)+"个聚类的信息如下：");
    		System.out.println("--------该邻接矩阵如下---------");
            g.print();
            System.out.println("结点的个数："+g.getNumOfVertice()+"  所有的节点："+g.getValueOfVertice());
            System.out.println("边的个数："+g.getNumOfEdges());
            System.out.println("第"+(clusterNum++)+"个聚类的信息结束\n==================================");
            vertices.clear();
    		weights.clear();
    		vecNum = 0;
    }
//    	System.out.println("cluster: "+cluster);
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
		CopyOfFeatureTraining ft = new CopyOfFeatureTraining();
		long before = System.currentTimeMillis();
		ft.getFeatrueCluster();
		//ft.mergeTempCluster();
		long current = System.currentTimeMillis();
		MyTime.gettime(current-before);
	}

}
