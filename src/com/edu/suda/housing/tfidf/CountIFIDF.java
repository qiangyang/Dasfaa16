package com.edu.suda.housing.tfidf;

import com.edu.suda.housing.dbmap.ReadDBMap;
import com.edu.suda.housing.dbmap.StoreDBMap;
import com.edu.suda.housing.tools.*;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.*;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

//import org.apache.lucene.analysis.Analyzer;  
//import org.apache.lucene.analysis.Token;  
//import org.apache.lucene.analysis.TokenStream;  
//import org.apache.lucene.analysis.tokenattributes.TermAttribute;  
//import org.apache.lucene.util.AttributeImpl;
//import org.wltea.analyzer.lucene.IKAnalyzer;

import jeasy.analysis.MMAnalyzer;
import database.DBconnection;
import file.MyTime;

public class CountIFIDF {
   
	public  HashMap <Integer, String> text = new HashMap<Integer, String>();
	public  HashMap <Integer, String[]> cutwords = new HashMap <Integer, String[]>();
	DBconnection db = DBconnection.getDbConnection();
	public  HashMap<String, Float> idf = new HashMap<String, Float>();
	public  Map<Integer, HashMap<String, Float>> tfidf = new HashMap<Integer, HashMap<String, Float>>();
	//统计所有的记录条数，并保存其id和信息
	public Map <Integer, String> getText(String sql){
	try {
		    ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				int id = rs.getInt("id");
				String description = rs.getString("房源描述");
				text.put(id, description);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //System.out.println("text"+text);
		return text;
	}
	
	//对指定id进行分词，将分词结果保存到字符串数组当中
    public   HashMap <Integer, String[]> cutWord(Map <Integer, String> map) throws IOException {
    	String[] cutWordResult = null;
    	Lexeme lex=null;
    	for(int i : map.keySet()){
    		String text = map.get(i);
    		StringReader sr=new StringReader(text);
    		StringBuffer sb = new StringBuffer();
    		IKSegmenter ik=new IKSegmenter(sr, true);
    		 while((lex=ik.next())!=null){  
    			 sb = sb.append(lex.getLexemeText()+"  ");  
    	        } 
    		 cutWordResult = sb.toString().split("  ");
    		 cutwords.put(i, cutWordResult);
    	}
        return cutwords;
    }
    
    //统计每篇文档中每个单词的tf（直接插入排序），将结果保存到HaspMap当中
    public   HashMap<Integer, HashMap<String, Float>> tf(HashMap <Integer, String[]> cutwords) {
    	HashMap<Integer, HashMap <String, Float>> tfvalue = new HashMap<Integer, HashMap <String, Float>>();
    	for(int id : cutwords.keySet()){
    		String []words = cutwords.get(id);
    		int singleIdWordNum = words.length;
    		HashMap<String, Float> tf = new HashMap<String, Float>();//正规化
    		int wordtf = 0;
            for (int i = 0; i < singleIdWordNum; i++) {
                wordtf = 0;
                for (int j = 0; j < singleIdWordNum; j++) {
                    if (words[i] != " " && i != j) {
                        if (words[i].equals(words[j])) {
                        	words[j] = " ";
                            wordtf++;
                        }
                    }
                }
                if (words[i] != " ") {
                    tf.put(words[i], (new Float(++wordtf)) / singleIdWordNum);
                    words[i] = " ";
                }
            }
            tfvalue.put(id, tf);
    	}
    	//System.out.println("tfvalue"+tfvalue);
        return tfvalue;
    }
    
  //统计每篇文档中每个单词出现的次数，将结果保存到hashmap中
    public   HashMap<Integer, HashMap <String, Integer>> normalTF(HashMap <Integer, String[]> cutwords) {
    	HashMap<Integer, HashMap <String, Integer>> tfvalue = new HashMap<Integer, HashMap <String, Integer>>();
    	for(int id : cutwords.keySet()){
    		HashMap<String, Integer> tfNormal = new HashMap<String, Integer>();//没有正规化
    		String []words = cutwords.get(id);
    		int singleIdWordNum = words.length;
    		int wordtf = 0;
    		for (int i = 0; i < singleIdWordNum; i++) {
                wordtf = 0;
                if (words[i] != " ") {
                    for (int j = 0; j < singleIdWordNum; j++) {
                        if (i != j) {
                            if (words[i].equals(words[j])) {
                            	words[j] = " ";
                                wordtf++;

                            }
                        }
                    }
                    tfNormal.put(words[i], ++wordtf);
                    words[i] = " ";
                }
            }
    		tfvalue.put(id, tfNormal);
    		//System.out.println("t"+tfvalue);
    	}
        return tfvalue;
    }
    
  //计算每个单词的idf的值，将结果存放到Map中
    public  HashMap<String, Float> idf(HashMap <Integer, String[]> cutwords) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //公式IDF＝log((1+|D|)/|Dt|)，其中|D|表示文档总数，|Dt|表示包含关键词t的文档数量。
            List<String> located = new ArrayList<String>();
            int Dt = 1;
            int D = text.size();//文档总数
            HashMap<Integer, HashMap<String, Integer>> tfInIdf = new HashMap<Integer, HashMap<String,Integer>>();
            tfInIdf = normalTF(cutwords);//存储各个文档tf的Map
            for(int id : tfInIdf.keySet()){
            	HashMap<String, Integer> temp = tfInIdf.get(id);
            	for(String word : temp.keySet()){
            		Dt = 1;
            		if (!(located.contains(word))){
            			for (int k = 1; k <= D; k++) {
            				//System.out.println("K"+k+"  id"+id);
            				if (k != id){
            					HashMap<String, Integer> temp2 = tfInIdf.get(k); 
            					if(temp2 == null) continue;
            					//System.out.println(temp2.size());
            					if (temp2.keySet().contains(word)) {
                                    located.add(word);
                                    Dt = Dt + 1;
                                    continue;
                                }
            				}
            			}
            			idf.put(word, Log.log((1 + D) / Dt, 10));
            		}
            	}
            }
       return idf;
    }
    
  //计算指定文件夹下每个文件中每个单词的tf-idf
    public  HashMap<Integer, HashMap<String, Float>> tfidf(HashMap <Integer, String[]> cutwords) throws IOException {
    	HashMap<Integer, HashMap<String, Float>> tf = tf(cutwords);
		for(int id : tf.keySet()){
			Map<String, Float> singelFile = tf.get(id);
			 for (String word : singelFile.keySet()) {
				 //System.out.println("idf.get(word)"+idf.get(word)+"  singelFile.get(word)"+singelFile.get(word));
	                singelFile.put(word, idf.get(word) * singelFile.get(word));
	            }
		}
        return tf;
    }

    public  Map<Integer, HashMap<String, Float>> getTFIDF(String sql){
    	CountIFIDF cii = new CountIFIDF();
		try {
			Map<Integer, HashMap<String, Float>> tf = cii.tf(cii.cutWord(cii.getText(sql)));
			HashMap<String, Float> idf = cii.idf(cii.cutWord(cii.getText(sql)));
			Map<Integer, HashMap<String, Float>> result= cii.tfidf(cii.cutWord(cii.getText(sql)));
			for(int i : result.keySet()){
				HashMap<String, Float> singletfidf = new HashMap<String, Float>();
				singletfidf = result.get(i);
				//System.out.println("房源id:   "+i+"=====Top30:"+MapUtil.sortByValue(singletfidf, MapUtil.SortType.DESC));
				singletfidf = (HashMap<String, Float>) MapUtil.sortByValue(singletfidf, MapUtil.SortType.DESC);
				tfidf.put(i, singletfidf);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tfidf;
    }
    
    public Map<Integer, HashMap<String, Float>> getTopKTfIdf(Map<Integer, HashMap<String, Float>> result){
    	for(int i : result.keySet()){
			HashMap<String, Float> singletfidf = new HashMap<String, Float>();
			singletfidf = result.get(i);
			result.put(i, (HashMap<String, Float>) MapUtil.sortByValue(singletfidf, MapUtil.SortType.DESC));
		}
		return result;   	
    }
	public static void main(String []args) throws IOException{
		//String sql = "select *from test_pre_ganji1";
		String sql = "select * from test_pre_ganji1";
		CountIFIDF cii = new CountIFIDF();
		long before = System.currentTimeMillis();
		try {
			HashMap<Integer, HashMap<String, Float>> tf = cii.tf(cii.cutWord(cii.getText(sql)));
//			HashMap<String, Float> idf = cii.idf(cii.cutWord(cii.getText(sql)));
			//输出tf
//			for(int id : tf.keySet()){
//				HashMap<String, Float> singletf = new HashMap<String, Float>();
//				singletf = tf.get(id);
//				for(String word : singletf.keySet()){
//					System.out.println("id:   "+id+"  "+"word:  "+word+"   "+"  tf:"+singletf.get(word));
//				}
//			}
			//输出idf
//			for(String word : idf.keySet()){
//					System.out.println("word:  "+word+"   "+"  idf:"+idf.get(word));
//			}
//			输出tf-idf
//			HashMap<Integer, HashMap<String, Float>> result = cii.tfidf(cii.cutWord(cii.getText(sql)));
//			for(int i : result.keySet()){
//				HashMap<String, Float> singletfidf = new HashMap<String, Float>();
//				singletfidf = result.get(i);
//				System.out.println("房源id:   "+i+"=====Top30:"+MapUtil.sortByValue(singletfidf, MapUtil.SortType.DESC));
//				for(String word : singletfidf.keySet()){
//					
//					System.out.println("id:   "+i+"  "+"word:  "+word+"   "+"  tfidf:"+singletfidf.get(word));
//				}
//			}
//            System.out.println("TopK"+cii.getTopKTfIdf(result));
			//StoreDBMap dbmap = new StoreDBMap();
		    //dbmap.savedata(result);
//			System.out.println("before"+result);
//		    ReadDBMap dbmapp = new ReadDBMap();
//		    System.out.println("after"+dbmapp.printdata());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long current = System.currentTimeMillis();
		MyTime.gettime(current-before);
	}
}
