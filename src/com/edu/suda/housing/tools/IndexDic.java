package com.edu.suda.housing.tools;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

import database.DBconnection;

//根据索引获取词典
public class IndexDic {
    public IndexDic(){}
    public static HashSet<String> dic = new HashSet<String>();
    public static HashSet indexDic(String tableName, String col){
    	DBconnection db = DBconnection.getDbConnection();
    	String sql = "select *from "+tableName;
    	try{
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				dic.add(rs.getString(col));
			}
			rs.close();
		}catch(Exception e){
			System.out.println("database read exception");
		}
    	return dic;
    }
    public static void main(String[]args){
    	System.out.println(indexDic("areaindex", "area"));
    }
}
