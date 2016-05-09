package com.edu.suda.housing.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

public class MyFilter {

	/*
	 * 过滤指定的文字
	 * */
	public static ArrayList<String> filter(ArrayList<String> s){
		ArrayList<String> array = new ArrayList<String>();
		for(int i=0;i<s.size();i++){
			//System.err.println("s"+readFileRow());
			if(s.get(i).length() != 1){
				if(!readFileRow().contains(s.get(i))){
					array.add(s.get(i));
				}
		  }
		}
		return  array;
	}
	/*
     * 督促文件的每一行数据
     * 
     * */
    public static String readFileRow(){
    	String str = "";
    	String str1 = "";
    	try{
    		File file = new File("filterWords.txt");
    		 if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
             FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
             BufferedReader br = new BufferedReader(isr);
             while((str = br.readLine())!=null) {
            	 str1 += str + " ";
             }
             isr.close();
             fis.close();
    	}catch(Exception e){
    		System.err.println("文件读写错误!"+e.toString());
    	}
    	return str1;
    }
	
    public static void main(String []args){
        System.out.println("温温".length());
    }
}
