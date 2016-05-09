package org.wltea.analyzer.test;

import java.io.IOException;  
import java.io.StringReader;  
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.wltea.analyzer.lucene.IKAnalyzer;  
  
public class Test {  
    public static void main(String[] args) throws IOException {  
        String text="10年大众速腾手动技术型，精品车况，动力强劲。推背感极强。内饰干净整洁、速腾大众的知名品牌是质量的保证，他所代表的不仅仅是一辆汽车，更重要的是这个企业的精神内含和文化底蕴 知名品牌, 值得信赖。外观大气稳重而不缺时尚 内饰做工严谨，室内空间宽敞. 一见速腾，刹哪间让人怦然心动 ，是每个男人的必选之一！ 配置；铝合金钢圈，电动大天窗，丝绒地毯，外后视镜防炫目镜，电动调节侧视镜，遥控防盗钥匙，高级自动恒温空调、多安全气囊激活系统和头颈保护系统，电子制动力分配系统，ABS防抱死、EBD、防侧滑、驻车距离警示系统等等！速腾维修方便便宜 ，历史悠久，保有量大，性价比高 ，车况质感非常好，前后原版，绝无事故，支持4S店检测,手续齐全合法，包转藉过户联系我时，请说明是在上海赶集网二手车看到的二手大众速腾，谢谢";  
        //创建分词对象  
        Analyzer anal=new IKAnalyzer(true);       
        StringReader reader=new StringReader(text);  
        //分词  
        TokenStream ts=anal.tokenStream("", reader);  
        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);  
        //遍历分词数据  
        while(ts.incrementToken()){  
            System.out.print(term.toString()+" ");  
        }  
        reader.close();  
        System.out.println();  
    }  
    public static ArrayList<String> segmentWords(String text) throws IOException{ 
    	ArrayList<String> arr = new ArrayList<String>();
        //创建分词对象  
        Analyzer anal=new IKAnalyzer(true);       
        StringReader reader=new StringReader(text);  
        //分词  
        TokenStream ts=anal.tokenStream("", reader);  
        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);  
        //遍历分词数据  
        while(ts.incrementToken()){
        	arr.add(term.toString());
            //System.out.print(term.toString()+" ");  
        }  
        reader.close();  
       // System.out.println();  
        return arr;
    }
    
  
}  