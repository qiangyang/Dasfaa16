package database;

import java.util.Properties;

/**
 * 数据库配置
 * @author zhoujian
 *
 */
public class config {
	/**
	 * 偏好配置文件
	 */
	private static Properties properties=new Properties();
	/**
	 * 偏好配置文件的路径
	 */
	private static String config_path="jdbc.properties";
	/**
	 * 数据库SQL解析类
	 */
	public static String db_Parser_Class;
	/**
	 * 数据库驱动
	 */
	public static String jdbc_Driver;
	/**
	 * jdbc链接的路径
	 */
	public static String jdbc_Url;
	/**
	 * 当前使用的数据库
	 */
	public static String jdbc_Database;
	/**
	 * 数据库使用的字符集
	 */
	public static String jdbc_Charset;
	/**
	 * 数据库用户名
	 */
	public static String jdbc_User;
	/**
	 * 数据库密码
	 */
	public static String jdbc_Password;
	/**
	 * 静态初始化偏好配置文件
	 */
	static{
		try {
			//加载输入流
			properties.load(config.class.getResourceAsStream(config_path));
			//获得配置各属性
			db_Parser_Class=properties.getProperty("db.parser.class");
			jdbc_Driver=properties.getProperty("jdbc.driver");
			jdbc_Url=properties.getProperty("jdbc.url");
			jdbc_Database=properties.getProperty("jdbc.database");
			jdbc_Charset=properties.getProperty("jdbc.charset");
			jdbc_User=properties.getProperty("jdbc.user");
			jdbc_Password=properties.getProperty("jdbc.pwd");
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("加载数据库配置文件失败："+e.toString());
		}
	}
	
}
