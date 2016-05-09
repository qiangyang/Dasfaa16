package com.edu.suda.housing.match;

import java.util.ArrayList;
import java.util.HashMap;

import com.edu.suda.housing.graph.MyAdjGraphic;
import com.edu.suda.housing.graph.Weight;
import com.edu.suda.housing.tools.MapUtil;

public class Cluster {
	public static HashMap<Integer,MyAdjGraphic> cluster = new HashMap<Integer,MyAdjGraphic>();
	public static HashMap<Integer,MyAdjGraphic> finalCluster = new HashMap<Integer,MyAdjGraphic>();
	FeatureTraining ft = new FeatureTraining();
	public double ThresholdRate = 0.6;
	/**
	 * 将短语之间的关联图根据权重转化为细粒度的类图
	 * @throws Exception 
	 * */
	public HashMap<Integer,MyAdjGraphic> getCluster() throws Exception{		
		cluster = ft.mergeTempCluster();
		for(int clusterId:cluster.keySet()){
			System.out.println("clusterId: "+clusterId);
			cluster.get(clusterId).print();
		}
		return finalCluster;
	}
	public void getMaxKPoint() throws Exception{
		cluster = ft.mergeTempCluster();
		for(int clusterId:cluster.keySet()){
			MyAdjGraphic g = cluster.get(clusterId);
			System.out.println("clusterId: "+clusterId+" max: "+MapUtil.sortValue(g.getPointWeightSum()));
		}
	}
	/**
	 * 根据每个节点的权重之和以及设定的阈值将图分为其子图
	 * @throws Exception 
	 * */
	public void splitGraph() throws Exception{
		 int clustersId = 1;
	   	 //构建子图的必备元素
	   	 ArrayList<String> vertices = new ArrayList<String>();
	   	 ArrayList<Weight> weights = new ArrayList<Weight>();
	   	 int vecNum = 0;
		cluster = ft.mergeTempCluster();
		for(int clusterId:cluster.keySet()){
			MyAdjGraphic g = cluster.get(clusterId);
		    int [][] edges = g.getEdges();
		    int allWeight = g.getWeightOfAllEdges();
		    HashMap<Integer, Integer> map = (HashMap<Integer, Integer>) MapUtil.sortValue(g.getPointWeightSum());
		    for(int pid: map.keySet()){

		    	if(map.get(pid)*1.0/allWeight > ThresholdRate){
		    		vertices.add(g.getValueOfVertice(pid));
		    		for(int i=0;i<map.size();i++){
			    		if(pid!=i){
			    			if(edges[pid][i]!=-1){
			    				if(map.get(i)*1.0/allWeight>ThresholdRate){
			    					  vecNum ++;
		        				      vertices.add(g.getValueOfVertice(i));
		        				      Weight w = new Weight(pid,i,edges[pid][i]);
		        				      weights.add(w);
			    				}
			    			}
			    		}
		    		}
		    		int n = vertices.size(); //顶点个数
		            int e = vecNum; //边个数
		            MyAdjGraphic temp = new MyAdjGraphic(n);
		            String[] vertice = (String[]) vertices.toArray(new String [vertices.size()]);
		            Weight[] weight = (Weight[]) weights.toArray(new Weight [weights.size()]);
		      	    Weight.createAdjGraphic(temp, vertice, n, weight, vecNum);
		      	    if(e>1){
		      	    	finalCluster.put(clustersId++, temp);
		      	    }
		      	      //恢复初始状态
			          vertices.clear();
		  		      weights.clear();
		  		      vecNum = 0;
		    	}
//            	System.out.println("节点: "+g.getValueOfVertice(pid)+"  rate: "+map.get(pid)*1.0/allWeight);
            }
		}
	}
	public static void main(String args[]) throws Exception{
		Cluster c = new Cluster();
//		c.getCluster();
//		c.getMaxKPoint();
		c.splitGraph();
	}
}
