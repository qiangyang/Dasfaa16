package test;

import house.House;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RecordToHouse {
	
     private LinkedHashMap<String, ResultSet> record=new LinkedHashMap<String, ResultSet>();
     
//     private ResultSet resultSet=null;

     private String tableName=null;
     
//     private ArrayList<House> houseList=new ArrayList<House>();
     
     private static ReadFromDB rDb=new ReadFromDB();
     
     private void getRecord(){
    	 this.record=this.rDb.getData(tableName);
     }
     
     public static ArrayList<House> recordToHouse(String tableName){
    	 ArrayList<House> houseList=new ArrayList<House>();
    	 ResultSet resultSet=rDb.getResultSet(tableName);
    	 houseList=recordToHouse(resultSet);
    	 return houseList;
     }
     
     public static ArrayList<House> recordToHouse(ResultSet resultSet){
    	 ArrayList<House> houseList=new ArrayList<House>();
    	 try {
			while(resultSet.next()){
				 House house=new House();
				 try {
					 house.id=resultSet.getInt("id");
					 house.source=resultSet.getString("来源");
					 house.title=resultSet.getString("标题");
					 house.crawlTime=resultSet.getString("抓取时间");
					 house.time=resultSet.getString("发布时间");
					 house.pay=resultSet.getString("租金");
					 house.payStyle=resultSet.getString("押付方式");
					 house.houseStyle=resultSet.getString("户型");
					 house.rentStyle=resultSet.getString("租凭方式");
					 house.style=resultSet.getString("房屋类型");
					 house.area=resultSet.getString("面积");
					 house.decoration=resultSet.getString("装修");
					 house.direction=resultSet.getString("朝向");
					 house.floor=resultSet.getString("楼层");
					 house.community=resultSet.getString("小区");
					 house.location=resultSet.getString("位置");
					 house.installation=resultSet.getString("配置");
					 house.contactMan=resultSet.getString("联系人");
					 house.contactWay=resultSet.getString("联系方式");
					 house.houseDetails=resultSet.getString("房源描述");
					 house.url=resultSet.getString("URL");
					 house.picNum=resultSet.getInt("图片数");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 houseList.add(house);
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return houseList;
     }

}
