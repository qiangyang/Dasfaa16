package com.edu.suda.housing.graph;

import java.util.ArrayList;
import java.util.HashMap;

//邻接矩阵类
public class MyAdjGraphic {

	static final int maxWeight=-1; //如果两个结点之间没有边，权值为-1；
    ArrayList<String> vertices = new ArrayList<String>();//存放结点的集合
    int[][] edges; //邻接矩阵的二维数组
    public int[][] getEdges() {
		return edges;
	}

	public void setEdges(int[][] edges) {
		this.edges = edges;
	}

	int numOfEdges; //边的数量
    int [][] maxKEdges;//最大的k个权值
    public MyAdjGraphic(int n)
    {
        edges = new int[n][n];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(i==j) //对角线上的元素为0
                {
                   edges[i][j]=0;    
                }
                else
                {
                   edges[i][j]=maxWeight;
                }
            }
        }
        numOfEdges = 0;
    }
    
    //返回边的数量
    public int getNumOfEdges()
    {
        return this.numOfEdges;
    }
    
    //返回结点的数量
    public int getNumOfVertice()
    {
        return this.vertices.size();
    }
    //返回所有的节点值
    public ArrayList<String> getValueOfVertice()
    {
        return this.vertices;    
    } 
    //返回结点的值
    public String getValueOfVertice(int index)
    {
        return this.vertices.get(index);    
    }
    
    //获得某条边的权值
    public int getWeightOfEdges(int v1,int v2) throws Exception
    {
       if((v1 < 0 || v1 >= vertices.size())||(v2 < 0||v2 >= vertices.size()))
       {
           throw new Exception("v1或者v2参数越界错误！");
       }
       return this.edges[v1][v2];
       
    }
  //获得所有边的权值之和
    public int getWeightOfAllEdges() throws Exception
    {
    	int weightSum = 0;
    	for(int i=0;i<vertices.size();i++)
    		for(int j=0;j<vertices.size();j++){
    			if(this.edges[i][j]!=-1){
    			weightSum+=this.edges[i][j];
    			}
    		}     
       return weightSum;      
    }
    //插入结点
    public void insertVertice(String obj)
    {
        this.vertices.add(obj);
    }
    
    //插入带权值的边
    public void insertEdges(int v1,int v2,int weight) throws Exception
    {
        if((v1 < 0 || v1 >= vertices.size())||(v2 < 0||v2 >= vertices.size()))
        {
          throw new Exception("v1或者v2参数越界错误！");
        }
        
        this.edges[v1][v2]=weight;
        this.numOfEdges++;
    }
    
    //删除某条边
    public void deleteEdges(int v1,int v2) throws Exception
    {
        if((v1 < 0 || v1 >= vertices.size())||(v2 < 0||v2 >= vertices.size()))
        {
          throw new Exception("v1或者v2参数越界错误！");
        }
        if( v1==v2 || this.edges[v1][v2]==maxWeight)//自己到自己的边或者边不存在则不用删除。    
        {
            throw new Exception("边不存在！");
        }
        
        this.edges[v1][v2]=maxWeight;
        this.numOfEdges--;   
    }
    
    //打印邻接矩阵
    public void print()
    {
        for(int i=0;i<this.edges.length;i++ )
        {
            for(int j=0;j<this.edges[i].length;j++)
            {
                System.out.print(edges[i][j]+"  ");    
            }
            System.out.println();
        }
    }

  //获得邻接矩阵中每个节点的度
    public HashMap<Integer, Integer> getPointWeightSum()
    {
    	HashMap<Integer, Integer> pointWeight = new HashMap<Integer, Integer>();
    	for(int k=0; k<vertices.size();k++){
    		int weightSum = 0;
        	//String point = vertices.get(k);
        	//行
        	for(int j=0;j<this.edges.length;j++){
        		if(edges[k][j]!= -1){
        		weightSum+= edges[k][j];
        		}
        	}
        	//列
        	for(int i=0; i<this.edges.length;i++){
        		if(edges[i][k]!= -1){
        		  weightSum+= edges[i][k];
        	    }
        	}
        	pointWeight.put(k, weightSum);
    	}
        return pointWeight;
    }
}
