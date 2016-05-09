package com.edu.cn.sua.housing.groundtruth;

import file.MyTime;
import house.House;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import database.DBconnection;

public class GroundTruth {

	public DBconnection db = DBconnection.getDbConnection();
	//data, bj_ganji, cd_ganji, su_ganji, sz_ganji, tj_ganji
	public String sql = "select *from data";
	//groundtruth_example, groundtruth_bj, groundtruth_cd, groundtruth_su, groundtruth_sz, groundtruth_tj
	public String fileName = "results/groundtruth_data.txt";
	public int mapZX(String s){
		int flag = 0;
		if(s.equals("豪华装修")){
			flag = 1;
		}else if(s.equals("精装修")){
			flag = 2;
		}else if(s.equals("中等装修")){
			flag = 3;
		}else if(s.equals("简单装修")){
			flag = 4;
		}else if(s.equals("毛坯")){
			flag = 5;
		}
		return flag;
	}

	public void getGroundTruth() throws SQLException,IOException{
		int same = 0;
		double rent1 = 0.0,rent2 = 0.0;
		int id1=0,id2 = 0;
		int zhuangxiu1 = 0, zhuangxiu2 = 0;
		ResultSet rs1 = db.executeQuery(sql);
		ResultSet rs2 = db.executeQuery(sql);
		File file=new File(fileName);
		int m=0,n=0;
		   while(rs1.next()){
			   same = 0;
			   m++;
			   id1 = rs1.getInt("id");
			   if(rs1.getString("租金")!=""){
//				   System.out.println("test"+rs1.getString("租金").substring(0, rs1.getString("租金").indexOf('元')));
				   //rent1 = Double.parseDouble(rs1.getString("租金").substring(0, rs1.getString("租金").indexOf('元')));
				   rent1 = Double.parseDouble(rs1.getString("租金"));
			   }else{
				   rent1 = 0.0;
			   }
			   if(rs1.getString("装修")!=""){
				   zhuangxiu1 = mapZX(rs1.getString("装修"));
			   }else{
				   zhuangxiu1 = -1;
			   }
			   String d1 = rs1.getString("朝向");	
			   if(d1==""){
				   same = 0;
				   break;
			   }
			   while(rs2.next()){
				   same = 0;
				   id2 = rs2.getInt("id");
				   if(rs2.getString("租金")!=""){
//					   System.out.println("test"+rs2.getString("租金").substring(0, rs2.getString("租金").indexOf('元')));
					   //rent1 = Double.parseDouble(rs2.getString("租金").substring(0, rs2.getString("租金").indexOf('元')));
					   rent2 = Double.parseDouble(rs2.getString("租金"));
				   }else{
					   rent2 = 0.0;
				   }
				   if(rs2.getString("装修")!=""){
					   zhuangxiu2 = mapZX(rs2.getString("装修"));
				   }else{
					   zhuangxiu2 = -1;
				   }
				   String d2 = rs2.getString("朝向");				   
				   //为空直接返回0
				   if(d2==""){
					   same = 0;
					   break;
				   }
				   
				   if(rs1.getString("编码").equals(rs2.getString("编码"))){
					   if(id1 != id2 && id1<id2){
						   if(rs1.getString("URL").equals(rs2.getString("URL"))){
							   same = 1;
						   }else{
							   if(Math.abs(rent1-rent2)>500){
								   same = 0;
							   }else{
								   if(Math.abs(zhuangxiu1-zhuangxiu2)>=2){
									   same = 0;
								   }else{
									   if(d1.length()==d2.length() && d1.equals(d2)){
										   same = 1;
									   }else if(d1.length()!=d2.length()){
										   if(d1.length()>d2.length()){
											   if(d1.contains(d2.charAt(1)+"")){
												   same = 1;
											   }else{
												   same = 0;
											   }
										   }else{
	                                           if(d2.contains(d1.charAt(1)+"")){
	                                        	   same = 1;
											   }else{
												   same = 0;
											   }
										   }
									   }
									   else{
										   same = 0;
									   }
								   }
							   }
						   }
					   }
			      }else{
			    	  same = 0;
			      }
				   if(same == 1 && id1<id2){
					   n++;
					   if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
			   		   FileOutputStream out = new FileOutputStream(file,true);
				   		StringBuffer sb = new StringBuffer();
		    			sb.append(id1+" "+id2+"\n");
		    			out.write(sb.toString().getBytes("utf-8"));
		    			out.close();
				   }
			   }
			   rs2.beforeFirst();
		  }
		   System.out.println("共有"+m+"条记录，其中匹配的记录有"+n+"对,匹配成功率为:"+n*1.0/m);
		rs1.close();
		rs2.close(); 
	}
	public static void main(String[] args) throws SQLException, IOException {
		GroundTruth gt = new GroundTruth();
		long before = System.currentTimeMillis();
		gt.getGroundTruth();
		long current = System.currentTimeMillis();
		MyTime.gettime(current-before);
	}
}
