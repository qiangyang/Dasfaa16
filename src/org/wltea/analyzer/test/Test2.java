package org.wltea.analyzer.test;
import java.io.IOException;  
import java.io.StringReader;  
  
import org.wltea.analyzer.core.IKSegmenter;  
import org.wltea.analyzer.core.Lexeme;  
  
public class Test2 {  
      
    public static void main(String[] args) throws IOException {  
        String text="温馨、精致、便利、舒适，方寸空间的为的是追求一个更广阔、自由的精神空间的基础"; 
        String str="12款公路野兽X6M，全车美国贴珠光膜，黑色黑内，4.4双涡轮增压，[表情]匙启动，已经刷中文导航，抬头显示，天窗，五门电吸电尾门，座椅通风本公司另出售进口宾利 劳斯莱斯 路虎 奔驰 宝马 凌志各种超跑，接受置换，价格合理 ";
        //StringReader sr=new StringReader(text);  
        StringReader sr=new StringReader(str); 
        IKSegmenter ik=new IKSegmenter(sr, true);  
        Lexeme lex=null;  
        while((lex=ik.next())!=null){  
            System.out.print(lex.getLexemeText()+"|");  
        }  
    }   
}  
