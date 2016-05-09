package com.edu.suda.housing.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import database.DBconnection;

public class DFIndex {

	public DFIndex() {
		// TODO Auto-generated constructor stub
	}

	public static Map<String,String> dic = new HashMap<String,String>();
	public static String tableName [] = {"test_pre_ganji","test_pre_anjuke","test_pre_tongcheng"};
    /*
     * 遍历所有的数据获取DF字典
     * 
     */	
	public static Map<String,String> indexDic(){
    	DBconnection db = DBconnection.getDbConnection();
    	for(int i = 0; i<tableName.length; i++){
    		String sql = "select 小区, 位置  from "+tableName[i];
//    	String sql = "select 小区, 位置  from test_pre_ganji1";
        	try{
    			ResultSet rs = db.executeQuery(sql);
    			while(rs.next()){
    				if(!dic.containsKey(rs.getString("小区")) && rs.getString("小区")!=" " && rs.getString("位置")!=" "){
    					dic.put(rs.getString("小区"), rs.getString("位置"));
    				}else if(dic.containsKey(rs.getString("小区"))){
    					if(dic.get(rs.getString("小区")).length() < rs.getString("位置").length()){
    						dic.put(rs.getString("小区"), rs.getString("位置"));
    					}else{
    						continue;
    					}
    				}else{
    					continue;
    				}
    			}
    			rs.close();
    		}catch(Exception e){
    			System.out.println("database read exception"+e.toString());
    		}
    	} 	
    	return dic;
    }
    
    /*
     * 将得到的DF关系得到文件中
     * 
     * */
    public static void writeInFile(Map<String,String> map){
    	File file = null;
    	try{
    		file = new File("DFDic.txt");
    		if(!file.exists())
                file.createNewFile();
    		FileOutputStream out = new FileOutputStream(file,true); 
    		for(String str:map.keySet()){
    			StringBuffer sb = new StringBuffer();
    			sb.append(str+","+map.get(str)+"\n");
    			out.write(sb.toString().getBytes("utf-8"));
    		}
    		out.close();
    	}catch(Exception e){
    		System.err.println("文件读写错误!");
    	}
    }
    
    /*
     * 将词典中的内容写到Map中
     * 
     * */
    public static Map<String,String> getMap(){
    	Map<String,String> dicmap = new HashMap<String,String>();
    	try{
    		File file=new File("DFDic.txt");
    		 if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
             FileInputStream fis = new FileInputStream(file);
             byte[] buf = new byte[1024];
             while((fis.read(buf))!=-1){
            	 String s = new String(buf); 
                 String str[] = s.split(",");
                 dicmap.put(str[0].trim(), str[1].trim());
                 buf = new byte[1024];//重新生成，避免和上次读取的数据重复
             }
    	}catch(Exception e){
    		System.err.println("文件读写错误!");
    	}
    	System.out.println(dicmap);
    	return dicmap;
    }
    /*
     * 测试写入的数据
     * 
     * */
    public static String readFile(){
        StringBuffer sb = new StringBuffer();
    	try{
    		File file=new File("DFDic.txt");
    		 if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
             FileInputStream fis = new FileInputStream(file);
             byte[] buf = new byte[1024];
             while((fis.read(buf))!=-1){
                 sb.append(new String(buf));    
                 buf = new byte[1024];//重新生成，避免和上次读取的数据重复
             }
    	}catch(Exception e){
    		System.err.println("文件读写错误!");
    	}
    	System.out.println(sb.toString());
    	return sb.toString();
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		indexDic();
		writeInFile(dic);
		readFile();
	}

}
