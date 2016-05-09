package com.edu.suda.housing.match;

import file.MyTime;
import house.House;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import com.edu.suda.housing.feature.TextPreprocess;
import com.edu.suda.housing.graph.MyAdjGraphic;
import com.edu.suda.housing.tools.ConsineImproved;

import database.DBconnection;

public class FeatureMatching {

	private static final double Threshold = 0.55;
	private static final double ThresholdByPhase = 0.05;
	public static HashMap<Integer,MyAdjGraphic> cluster = new HashMap<Integer,MyAdjGraphic>(); 
	public DBconnection db = DBconnection.getDbConnection();
	public Map<String, LinkedList<House>> buckets = new HashMap<String, LinkedList<House>>();
//	public static String sql = "select * from groundtruth1";
	public static String tableName = "bj_ganji";
	public static String sql = "select *from data";
	BucketsInMatch bim = new BucketsInMatch();
	public static int allRecordsNum = 0;
	public static int matchedRecordNum = 0;
	public static int right;
	public static Map<Integer,String> matchedParis = new HashMap<Integer, String>();
	public String fileName = "results/groundtruth_data.txt";
	//存放临时聚类结果  groundtruth1  test_pre_ganji1
	
	/**
	 * 获得比较数据做一些处理工作
	 * @throws Exception  
	 * */
	public void dataPreprocess(ResultSet rs1,ResultSet rs2) throws Exception{
		FeatureTraining ft = new FeatureTraining();
		cluster = ft.mergeTempCluster();		
		LinkedList<ArrayList<String>> tem1 = new LinkedList<ArrayList<String>>();
		LinkedList<ArrayList<String>> tem2 = new LinkedList<ArrayList<String>>();
		ArrayList<Double> vec1 = new ArrayList<Double>();
		ArrayList<Double> vec2 = new ArrayList<Double>();
		double sim =0.0;
		try{
//			System.out.println("test");
			while(rs1.next()){
				allRecordsNum++;
//				System.out.println("allRecordsNum: "+allRecordsNum);
				int id1 = rs1.getInt("id");
				String des1 = rs1.getString("房源描述");
				  tem1 = TextPreprocess.getFeatureSet(TextPreprocess.getSubSentence(des1));
				  vec1 = clusterList(tem1);
//				  System.out.println("vec1: "+vec1);
				while(rs2.next()){
					int id2 = rs2.getInt("id");
					String des2 = rs2.getString("房源描述");
					  tem2 = TextPreprocess.getFeatureSet(TextPreprocess.getSubSentence(des2));
					  vec2 = clusterList(tem2);
//					  System.out.println("des1: "+TextPreprocess.getSubSentence(des1)+"  des2:"+TextPreprocess.getSubSentence(des2));
					  if(id1 != id2 && id1<id2){
						  if(vec1.size()>0 && vec2.size()>0){
							  sim = ConsineImproved.similarityByDouble(vec1, vec2);
							  System.out.println("编号为："+id1+"  和     "+"编号为：" +id2+"  的相似度为："+sim);
							  if(sim>Threshold){
//								  System.out.println("编号为："+id1+"  和     "+"编号为：" +id2+"  的相似度为："+sim);
								  String mp = id1+" "+id2;
								  matchedParis.put(matchedRecordNum, mp);
								  matchedRecordNum++;
							  }
						  }
					  }
					}
				//恢复rs2
				rs2.beforeFirst();
			}
			rs1.close();
			rs2.close();
			}catch(Exception e){
				System.out.println(e.toString());
			}
	}
	/**
	 * 构建特征向量基模板
	 * @throws Exception 
	 * */
	public HashMap<Integer,Integer> getVector() throws Exception{
		HashMap<Integer,Integer> vector = new HashMap<Integer,Integer>();
		for(int id:cluster.keySet()){
			MyAdjGraphic mg = cluster.get(id);
			  vector.put(id,mg.getWeightOfAllEdges());
		}
		return vector;
	}
	/**
	 * 判断某个分词短句所属的类别，并返回类别编号
	 * @param subSen 子句作为输入
	 * @return clusterId 类编号
	 * @throws Exception 
	 */
	public ArrayList<Double> clusterList(LinkedList<ArrayList<String>> tem) throws Exception{
		ArrayList<Integer> idList = new ArrayList<Integer>();
		ArrayList<Double> simList = new ArrayList<Double>();
		ArrayList<String> subSen = new ArrayList<String>();
		ArrayList<Double> vec = new ArrayList<Double>();
		HashMap<Integer,Integer> vector = new HashMap<Integer,Integer>();
		vector = getVector();
//		System.out.println(tem);
		for(int i=0; i<tem.size();i++){
			subSen = tem.get(i);
			double max = 0.0;
			int pos = -1;
			for(int id: cluster.keySet()){
				double sim = ConsineImproved.similarity(subSen,cluster.get(id).getValueOfVertice());
				  if(sim>max) {
					  if(sim>ThresholdByPhase){
					  max = sim;
					  pos = id;
				  }
			   }
			}
			if(pos>-1 && max>0){
				idList.add(pos);
				simList.add(max);
			}
			subSen.clear();
//			System.out.println("idList  "+idList+" simList "+simList);
		}
		if(idList.size()>0 && simList.size()>0){
			//统计相似度之和
			double allSim = 0.0;
	        for(int i=0;i<simList.size();i++){
	        	allSim+=simList.get(i);
	        }
//	        System.out.println("allSim:"+allSim);
	        //构建余弦向量
			for(int id:vector.keySet()){
				double tempSim = 0.0;
				for(int i1=0; i1<idList.size();i1++){
					if(id == idList.get(i1)){
						tempSim+=simList.get(i1);
					}
				}
//				System.out.println(vector.get(id));
				vec.add(tempSim/allSim*vector.get(id));
			}
		}
//		System.out.println("vec:"+vec);
		return vec;
	}
	
	/**
	 * get the house buckets based on code
	 * */
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
	
	/**
	 * 基于特征的匹配算法
	 * @throws Exception 
	 */
	public void matching() throws Exception{
		Map<String, LinkedList<House>> codeBuckets = getBuckets();
		//bim.getSmallBuckets();
		for(String code: codeBuckets.keySet()){
			String newsql = sql+" where 编码='"+code+"'";
			ResultSet rs1 = db.executeQuery(newsql);
			ResultSet rs2 = db.executeQuery(newsql);
			dataPreprocess(rs1,rs2);
		}
	}
	/**
	 * 计算pr值
	 * @throws Exception 
	 */
	public void getPrecisionRecall(){
		Double precision = 0.0;
		Double recall = 0.0;
		Double f1 = 0.0;
		int line = 1;
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;           
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
//                System.out.println("line " + line + ": " + tempString);
                for(int key:matchedParis.keySet()){
        			if(tempString.equals(matchedParis.get(key))){
//        				System.out.println(tempString+ "   "+matchedParis.get(key));
        				right++;
        			}
        		} 
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        System.out.println("实际匹配的个数="+right+"  理论匹配个数="+matchedRecordNum+" 总的个数="+line);
        precision = right*1.0/matchedRecordNum;
        recall = right*1.0/line;
        f1 = 2*precision*recall/(precision+recall);
        System.out.println("准确率="+precision+ "  召回率="+recall+" f1="+f1);
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FeatureMatching fm = new FeatureMatching();
		long before = System.currentTimeMillis();
		fm.matching();
		long current = System.currentTimeMillis();	
		fm.getPrecisionRecall();
//		System.out.println("总的记录个数为："+allRecordsNum+"   匹配的记录个数为："+matchedRecordNum);
		MyTime.gettime(current-before);
	}

}
