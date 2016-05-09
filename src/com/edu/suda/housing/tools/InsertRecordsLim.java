package com.edu.suda.housing.tools;
import database.*;
import java.sql.*;
import org.apache.log4j.Logger;
import com.mysql.jdbc.log.Log;

public class InsertRecordsLim {
	/*将一个表中的数据导入到另外一个表中*/
	static Logger logger = Logger.getLogger(InsertRecordsLim.class.getName()); 
	DBconnection db = DBconnection.getDbConnection();
	public void insertTwoTable(String ta){
	 try{
		ResultSet rs =db.executeQuery(ta);
		int num = 0;
		while(rs.next() && num <=100){
			String description = rs.getString("房源描述");
			db.executeUpdate("insert into ganji_100 values("+description+")");
			num++;
		}
	 }catch(SQLException s){
		 
	 }
	}
	
	public static void main(String args[]){
		String sql = "select *from ganji";
		InsertRecordsLim irl = new InsertRecordsLim();
		irl.insertTwoTable(sql);
	}
}
