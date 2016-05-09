package com.edu.suda.housing.feature;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.test.Test;

import com.edu.suda.housing.tools.MyFilter;

public class TextPreprocess {

	/**
	 *将句子根据标点符号分解为子句 
     */
	public static String[] getSubSentence(String text){
		String[] subStr = text.split("，|。|：|！|。|\\？|\\?|!|,|\\.|:");
		//System.err.println("subStr"+subStr.length);
//		for(int i=0;i<subStr.length;i++){
//			System.out.print(" "+subStr[i]+" ");
//		}
		return subStr;
	}
	/**
	 *获取文本信息的特征集合 
     */
	public static LinkedList<ArrayList<String>> getFeatureSet(String[] str) throws IOException{
		LinkedList<ArrayList<String>> arr = new LinkedList<ArrayList<String>>();
		for(int i=0;i<str.length;i++){		
			ArrayList<String> s = MyFilter.filter(Test.segmentWords(str[i]));
			if(s.size() > 0)
			   arr.add(s);
			else
			   continue;
//			for(int j=0;j<s.size();j++){
//				System.out.println(s.get(j));
//			}
//			System.out.println("====================");
		}
		return arr;
	}
	
	public static void main(String[]args) throws IOException{
		TextPreprocess text = new TextPreprocess();
		String str[] = text.getSubSentence("房源描述附近房源推荐地图?街景小区简介经纪人向您承诺：该房源待租、实拍照片、真实价格、真实描述，如您发现信息虚假，请立即举报?房屋配置：我是房东免中介费这边是精装的公寓！卖的是装修好家具家电齐全和交通方便！面积都不大！");
		System.out.println(text.getFeatureSet(str));
	}
}
