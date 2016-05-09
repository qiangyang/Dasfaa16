package com.edu.suda.housing.tools;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 * 计算两个句子（文本）的相似性
*/
public class ConsineImproved {
	private static final double Threshold = 0.3;
	public static double getSimilarity(ArrayList<String> doc1, ArrayList<String> doc2){
		double result = 0.0;
		if (doc1 != null && doc1.size() > 0 && doc2 != null && doc2.size() > 0) { 
			//int max = doc1.size()>doc2.size()?doc1.size():doc2.size();
			
			ArrayList<Integer> find1 = new ArrayList<Integer>();
	    	ArrayList<Integer> find2 = new ArrayList<Integer>(); 
	    	    	
	    	double sum = 0.0;
			int n = 0;//,i = 0,j = 0;
//			ListIterator<String> lt1 = doc1.listIterator();		
//			ListIterator<String> lt2 = doc2.listIterator();
			for(int i=0;i<doc1.size();i++){
//			while(lt1.hasNext()){
//				 String str1 = (String) lt1.next();
				double maxValue =0.0;
				if(!find1.contains(i)){
//					 while(lt2.hasNext()){
//						 String str2 = (String) lt2.next();
				for(int j=0;j<doc2.size();j++){
					if(!find2.contains(j)){
						if(maxValue <getSimilarityByString(doc1.get(i), doc2.get(j))){
							maxValue = getSimilarityByString(doc1.get(i), doc2.get(j));
							n = j;
						}	
					}
//					j++;
				}
				if(maxValue>Threshold){
					find1.add(i);
					find2.add(n);
					sum += maxValue;
				}
			  }
//				i++;
			}
			result = sum/Math.min(doc1.size(), doc2.size());
		}
		return result;
	}
	/**
	 * 以短语为单位计算余弦相似度
	 * */
	public static double similarity(ArrayList<String> doc1, ArrayList<String> doc2){
		HashSet<String> wordSet = new HashSet<String>();
		ArrayList<Integer> vector1 = new ArrayList<Integer>();
    	ArrayList<Integer> vector2 = new ArrayList<Integer>();
		double similarity = 0.0;
    	double numcrator = 0.0;
    	double denominator = 1.0;
    	double temp1 =0.0, temp2 = 0.0;
    	for(String str: doc1){
    		wordSet.add(str);
    	}
    	for(String str: doc2){
    		wordSet.add(str);
    	}
    	for(String w1: wordSet)
		{
			if(doc1.contains(w1)){
				vector1.add(1);
			}else{
				vector1.add(0);
			}
		}
		for(String w2: wordSet)
		{
			if(doc2.contains(w2)){
				vector2.add(1);
			}else{
				vector2.add(0);
			}
		}	
		for(int k = 0; k<wordSet.size(); k++){
    		numcrator += vector1.get(k)*vector2.get(k);
    		temp1 += Math.pow(vector1.get(k), 2.0);
    		temp2 += Math.pow(vector2.get(k), 2.0);
    	}
    	denominator = Math.sqrt(temp1)*Math.sqrt(temp2);
    	similarity = numcrator/denominator;	
        return  similarity;   	
	}
	/**
	 * 以数值为单位计算余弦相似度
	 * */
	public static double similarityByDouble(ArrayList<Double> d1, ArrayList<Double> d2){
		double similarity = 0.0;
    	double numcrator = 0.0;
    	double denominator = 1.0;
    	double temp1 =0.0, temp2 = 0.0;

//    	System.out.println(d1+"      "+d2);
        if(d1.size()<0 || d2.size()<0 || d1.size()!=d2.size()) return 0;
        else{			
			for(int k = 0; k<d1.size(); k++){
	    		numcrator += d1.get(k)*d2.get(k);
	    		temp1 += Math.pow(d1.get(k), 2.0);
	    		temp2 += Math.pow(d2.get(k), 2.0);
	    	}
	    	denominator = Math.sqrt(temp1)*Math.sqrt(temp2);
	    	similarity = numcrator/denominator;	
        }
        return  similarity;   	
	}
	/**
	 * 以短语为单位计算相似度
	 * */
	public double getSimilarityByPhrase(String doc1, String doc2){
		if(doc1.equals(doc2)) return 1;
		else return 0;
	}
	/**
	 * 以汉字为单位计算相似度
	 * */
    public static double getSimilarityByString(String doc1, String doc2) {
        if (doc1 != null && doc1.trim().length() > 0 && doc2 != null
                && doc2.trim().length() > 0) {
             
            Map<Integer, int[]> AlgorithmMap = new HashMap<Integer, int[]>();
             
            //将两个字符串中的中文字符以及出现的总数封装到，AlgorithmMap中
            for (int i = 0; i < doc1.length(); i++) {
                char d1 = doc1.charAt(i);
                if(isHanZi(d1)){
                    int charIndex = getGB2312Id(d1);
                    if(charIndex != -1){
                        int[] fq = AlgorithmMap.get(charIndex);
                        if(fq != null && fq.length == 2){
                            fq[0]++;
                        }else {
                            fq = new int[2];
                            fq[0] = 1;
                            fq[1] = 0;
                            AlgorithmMap.put(charIndex, fq);
                        }
                    }
                }
            }
 
            for (int i = 0; i < doc2.length(); i++) {
                char d2 = doc2.charAt(i);
                if(isHanZi(d2)){
                    int charIndex = getGB2312Id(d2);
                    if(charIndex != -1){
                        int[] fq = AlgorithmMap.get(charIndex);
                        if(fq != null && fq.length == 2){
                            fq[1]++;
                        }else {
                            fq = new int[2];
                            fq[0] = 0;
                            fq[1] = 1;
                            AlgorithmMap.put(charIndex, fq);
                        }
                    }
                }
            }
             
            Iterator<Integer> iterator = AlgorithmMap.keySet().iterator();
            double sqdoc1 = 0;
            double sqdoc2 = 0;
            double denominator = 0; 
            while(iterator.hasNext()){
                int[] c = AlgorithmMap.get(iterator.next());
                denominator += c[0]*c[1];
                sqdoc1 += c[0]*c[0];
                sqdoc2 += c[1]*c[1];
            }
             //System.out.println("str: "+denominator / Math.sqrt(sqdoc1*sqdoc2));
            return denominator / Math.sqrt(sqdoc1*sqdoc2);
        } else {
            throw new NullPointerException(
                    " the Document is null or have not cahrs!!");
        }
    }
 
    public static boolean isHanZi(char ch) {
        // 判断是否汉字
        return (ch >= 0x4E00 && ch <= 0x9FA5);
 
    }
 
    /**
     * 根据输入的Unicode字符，获取它的GB2312编码或者ascii编码，
     * 
     * @param ch
     *            输入的GB2312中文字符或者ASCII字符(128个)
     * @return ch在GB2312中的位置，-1表示该字符不认识
     */
    public static short getGB2312Id(char ch) {
        try {
            byte[] buffer = Character.toString(ch).getBytes("GB2312");
            if (buffer.length != 2) {
                // 正常情况下buffer应该是两个字节，否则说明ch不属于GB2312编码，故返回'?'，此时说明不认识该字符
                return -1;
            }
            int b0 = (int) (buffer[0] & 0x0FF) - 161; // 编码从A1开始，因此减去0xA1=161
            int b1 = (int) (buffer[1] & 0x0FF) - 161; // 第一个字符和最后一个字符没有汉字，因此每个区只收16*6-2=94个汉字
            return (short) (b0 * 94 + b1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void main(String[]args){
    	//System.out.println(ConsineImproved.getSimilarityByString("坐北朝南","朝东"));
    	ArrayList<String> doc1 = new ArrayList<String>();
    	ArrayList<String> doc2 = new ArrayList<String>();
    	doc1.add("空调");
    	doc1.add("冰箱");
    	doc1.add("网");
    	doc2.add("空调");
    	doc2.add("冰柜");
    	doc2.add("网络");
    	doc2.add("朝南");
    	System.out.println(ConsineImproved.getSimilarity(doc1,doc2));
    }
}