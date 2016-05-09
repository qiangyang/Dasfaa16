package test;

import file.MyTime;
import house.House;
import house.PreHouse;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import database.DBconnection;

public class PreDeal2 {
	private static DBconnection dBconnection=DBconnection.getDbConnection();
	
	private static ArrayList<House> houseList=new ArrayList<House>();
	
	private static ArrayList<PreHouse> prehouseList=new ArrayList<PreHouse>(); 
	
	public static ArrayList<PreHouse> getPreHouse(String tableName){
		houseList=RecordToHouse.recordToHouse(tableName);
		for(House house:houseList){
			PreHouse preHouse=new PreHouse();
			if(house.pay.contains("元/月")){
				int index=house.pay.indexOf("元");
//				System.out.println(house.pay+index);
				int index1=house.pay.indexOf(".");
				if(index1>0){
					preHouse.pay=Integer.parseInt(house.pay.substring(0, index1).trim());
				}
				else {
					preHouse.pay=Integer.parseInt(house.pay.substring(0, index).trim());
				}
			}
			else if(house.pay.contains("元/年")){
				int index=house.pay.indexOf("元");
//				System.out.println("pay:"+house.pay+"/"+index);
				int index1=house.pay.indexOf(".");
				if(index1>0){
					preHouse.pay=Integer.parseInt(house.pay.substring(0, index1).trim())/12;
				}
				else {
					preHouse.pay=Integer.parseInt(house.pay.substring(0, index).trim())/12;
				}
			}
			else if(house.pay=="0"||house.pay.equals("0")){
				preHouse.pay=0;
			}
			preHouse.community=PreDeal2.stringFilter(house.community).trim();
			
			preHouse.area=house.area.replace("㎡", "").replace("平米", "").trim();
			int index=preHouse.area.indexOf(".");
			if(index>0){
				preHouse.area=preHouse.area.substring(0, index).trim();
			}
			
			preHouse.id=house.id;
			preHouse.title=house.title.trim();
			preHouse.crawlTime=house.crawlTime.replaceAll("-", "").replace("年", "").replace("月", "").replace("日", "").trim();
			preHouse.time=house.time.trim();
			preHouse.source=house.source.trim();

			preHouse.payStyle=house.payStyle.trim();
			preHouse.houseStyle=house.houseStyle.trim();
			preHouse.rentStyle=house.rentStyle.trim();
			preHouse.style=house.style.trim();

			preHouse.decoration=house.decoration.trim();
			preHouse.direction=house.direction.trim();
			preHouse.floor=house.floor.replace("层", "").trim();
			
			preHouse.location=house.location.replace("苏州-", "").trim().replace(" ", "-");
			preHouse.installation=house.installation.trim();
			preHouse.contactMan=house.contactMan.trim();
			preHouse.contactWay=house.contactWay.trim();
			preHouse.houseDetails=PreDeal2.stringFilter2(house.houseDetails.trim());
			preHouse.url=house.url.trim();
			preHouse.picNum=house.picNum;
			
			prehouseList.add(preHouse);
		}
		return prehouseList;
	}
	//存入数据库,pre为表前缀
	public static void storePreHouse(String tableName){
		int count=0,total=prehouseList.size(),i;
		for(PreHouse preHouse:prehouseList){
			String sql="insert IGNORE into"+" "+"pre_"+tableName+"(id,来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,配置,联系人,联系方式,房源描述,图片数,URL)"
					+" "+"values("+preHouse.id+","+"'"+preHouse.source+"'"+","+"'"+preHouse.title+"'"+","+"'"+preHouse.crawlTime+"'"+","
					+"'"+preHouse.time+"'"+","+preHouse.pay+","+"'"+preHouse.payStyle+"'"+","+"'"+preHouse.houseStyle+"'"+","
					+"'"+preHouse.rentStyle+"'"+","+"'"+preHouse.area+"'"+","+"'"+preHouse.style+"'"+","+"'"+preHouse.decoration+"'"+","
					+"'"+preHouse.direction+"'"+","+"'"+preHouse.floor+"'"+","+"'"+preHouse.community+"'"+","
					+"'"+preHouse.location+"'"+","+"'"+preHouse.installation+"'"+","+"'"+preHouse.contactMan+"'"+","+"'"+preHouse.contactWay+"'"+","
					+"'"+preHouse.houseDetails+"'"+","+preHouse.picNum+","+"'"+preHouse.url+"'"+")";
//			System.out.println(sql);
			i=dBconnection.executeUpdate(sql);
			if(i>0){
				count++;
//				System.out.println(i+"条数据插入成功！");
			}
			else {
//				System.out.println("房源数据插入失败!");
//				System.out.println(sql);
			}
			
		}
		System.out.println("需预处理："+total);
		System.out.println("预处理了："+count);
		System.out.println("预处理表中已有："+(total-count));
	}
	
	//过滤特殊字符
	public static String stringFilter(String str) throws PatternSyntaxException{
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？·@&?]";
//		String regEx="[^u4e00-u9fa5]";		
		Pattern pattern=Pattern.compile(regEx);
		Matcher matcher=pattern.matcher(str);
		return matcher.replaceAll("").trim();
	}
	
	//过滤奇异字符
	public static String stringFilter2(String str) throws PatternSyntaxException{
		String regEx="'?";
//		String regEx="[^u4e00-u9fa5]";		
		Pattern pattern=Pattern.compile(regEx);
		Matcher matcher=pattern.matcher(str);
		return matcher.replaceAll("").trim();
	}
	
	
	public static void clear() {
		houseList.clear();
		prehouseList.clear();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start=System.currentTimeMillis();
//		String[] tableName={"ganji","tongcheng","anjuke"};
//		for(int i=0;i<3;i++){
//			PreDeal2.clear();
//			System.out.println("houseList:"+PreDeal2.houseList);
//			System.out.println("prehouseList:"+PreDeal2.prehouseList);
//			System.out.println("预处理:"+tableName[i]);
//			PreDeal2.getPreHouse(tableName[i]);
//			PreDeal2.storePreHouse(tableName[i]);
//		}
		System.out.println("houseList:"+PreDeal2.houseList);
		System.out.println("prehouseList:"+PreDeal2.prehouseList);
		System.out.println("预处理:"+"tongcheng");
		PreDeal2.getPreHouse("tongcheng");
		PreDeal2.storePreHouse("tongcheng");
//		String string="??100%真实房源??看房热线：183-5111-9503??小郭QQ:710823508?真诚为您而服务?期待您的来电??????????????????????????'链屋中国租售中心?'?帮助您在寻家路上的快乐??????????????????????链屋100%真房源向您郑重承诺????????公司介绍：我们链屋中国租售中心的员工贴心为您服务，让您在繁琐的二手房交易过程中更加'安心、顺心、省心、放心'！所有房源'真实有效、每周更新、亲自实地拍摄'我们不吃差价,只赚取合理的佣金,让您安心。我们公司有一条龙的服务,从看房、签约、贷款办理、过户、房屋交接、水电煤交接,都有我们公司专业的专员";
//		String str=stringFilter2(string);
//		System.out.println(str);
	}

}
