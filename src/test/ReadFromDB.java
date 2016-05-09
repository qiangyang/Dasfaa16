package test;

import java.sql.ResultSet;
import java.util.LinkedHashMap;

import com.mysql.jdbc.Statement;

import database.DBconnection;

public class ReadFromDB {
	private DBconnection dBconnection=DBconnection.getDbConnection();
    private String tableName=null;
	private ResultSet resultSet=null;
	private String sqlString="";
	private LinkedHashMap<String, ResultSet> record=new LinkedHashMap<String, ResultSet>();
	
	public ResultSet getResultSet(){
		return this.resultSet;
	}
	
	public String getSqlString(){
		return this.sqlString;
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	public void setTableName(String tableName){
		this.tableName=tableName;
	}
	
	public void setSqlString(String tableName){
		this.setTableName(tableName);
		this.sqlString="select * from "+this.tableName;
	}
	/**
	 * 
	 * @method 返回一张表的所有记录  以表名-结果
	 */
	public LinkedHashMap<String, ResultSet> getData(String tableName){
		this.resultSet=this.getResultSet();
		this.record.put(tableName, resultSet);
		return this.record;
	}
	/**
	 * 
	 * @method 返回一张表的所有记录
	 */
	public ResultSet getResultSet(String tableName){
		this.setSqlString(tableName);
		this.resultSet=this.dBconnection.executeQuery(this.sqlString);
		return this.resultSet;
	}
	/**
	 * 
	 * @method 返回一张表的所有记录
	 */
	public ResultSet getResultSet(String tableName,String sql){
		this.resultSet=this.dBconnection.executeQuery(sql);
		return this.resultSet;
	}
	/**
	 * 
	 * @method 获取小区
	 */
	public ResultSet getCommunity(String tableName){
		ResultSet rs=null;
		String sql="select DISTINCT(小区),位置 from"+tableName;
		rs=this.dBconnection.executeQuery(sql);
		return rs;
	}
	
	
}
