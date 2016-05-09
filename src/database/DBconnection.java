package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnection {
	/**
	 * 实例
	 */
	private static DBconnection dbconnection;
	/**
	 * 数据库信息
	 */
	private DatabaseMetaData dbmd;
	/**
	 * 数据库连接
	 */
	private Connection connection;
	/**
	 * 执行操作statement
	 */
	private Statement statement;
	/**
	 * 构造函数
	 */
	private DBconnection(){
		try {
			Class.forName(config.jdbc_Driver);
			this.connection=DriverManager.getConnection(config.jdbc_Url+config.jdbc_Database+"?"+config.jdbc_Charset,
					config.jdbc_User,config.jdbc_Password);
			this.dbmd=this.connection.getMetaData();
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			System.err.println("未找到数据库连接类："+e.toString());
		}catch (SQLException e) {
			// TODO: handle exception
			System.err.println("数据库连接错误："+e.toString());
		}
	}
	/**
	 * 获取数据库连接实例
	 * @return dbconnection实例
	 */
	public static synchronized DBconnection getDbConnection() {
		if (null == dbconnection) {
			dbconnection = new DBconnection();
		}
		return dbconnection;
	}
	
	public void createDB(){
		String sql="CREATE DATABASE if not exists `house` DEFAULT CHARSET=utf-8";
		this.executeUpdate(sql);
	}
	
	/**
	 * 更新操作update
	 * 
	 * @param sql
	 * @return int 受影响的行数
	 */
	public int executeUpdate(String sql){
		int row=-1;
		try {
			this.statement=this.connection.createStatement();
			row=statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println("SQL 错误："+e.toString());
		}
		return row;
	}
	
	/**
	 * 执行查询query
	 * 
	 * @param  sql 查询SQL
	 * @return ResultSet 查询结果集
	 */
	public ResultSet executeQuery(String sql){
		ResultSet result=null;
		try {
			this.statement=this.connection.createStatement();
			result=this.statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println("SQL 错误："+e.toString());
		}
		return result;
	}
	

	
	public boolean hasTable(String tableName){
		boolean flag=false;
		try{
			DatabaseMetaData metaData=this.connection.getMetaData();
			ResultSet rs=metaData.getTables(null, null, tableName, null);
			while(rs.next()){
				flag=true;
			}
		}catch(Exception eHasTable){
			System.err.println(eHasTable);
			eHasTable.printStackTrace();
		}
		return flag;
	}
	
	public void createTable(String tableName){
		
	}
	
	public void close(){
		try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBconnection.getDbConnection().createDB();
		
	}
}
