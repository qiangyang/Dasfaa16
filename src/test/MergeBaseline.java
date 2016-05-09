package test;

import file.MyTime;
import house.Community;
import house.House;
import house.PreHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import database.DBconnection;

public class MergeBaseline {
	private DBconnection dBconnection=DBconnection.getDbConnection();
	
	private LinkedHashMap<String, PreHouse> prehosuesMap=new LinkedHashMap<String, PreHouse>();
	
	private LinkedHashMap<String, Community> communitiesMap=new LinkedHashMap<String, Community>();
	
	private LinkedHashMap<String, String> mergeId=new LinkedHashMap<String, String>();
	
	private static ArrayList<House> houseList=new ArrayList<House>();
	
	private static ArrayList<PreHouse> prehouseList=new ArrayList<PreHouse>(); 
	
	private LinkedHashMap<String, String> communtiesIndex=new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> locationsIndex=new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> houseStylesIndex=new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> areasIndex=new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> floorsIndex=new LinkedHashMap<String, String>();
	
	public MergeBaseline(){
		initIndex("communityIndex", communtiesIndex);
		initIndex("locationIndex", locationsIndex);
		initIndex("houseStyleIndex", houseStylesIndex);
		initIndex("areaIndex", areasIndex);
		initIndex("floorIndex", floorsIndex);
	}
	
	//读取记录,简单过滤,未融合数据
	public static void houseInfo(String tableName){
//		houseList.clear();
		houseList=RecordToHouse.recordToHouse(tableName);
		for(House house:houseList){
			PreHouse preHouse=new PreHouse();
			if(house.pay.contains("月")){
				int index=house.pay.indexOf("元");
//				System.out.println(house.pay+index);
				preHouse.pay=Integer.parseInt(house.pay.substring(0, index).trim());
				
			}
			else if(house.pay.contains("年")){
				int index=house.pay.indexOf("元");
//				System.out.println("pay:"+house.pay+"/"+index);
				preHouse.pay=Integer.parseInt(house.pay.substring(0, index).trim())/12;
			}
			else if(house.pay=="0"||house.pay.equals("0")){
				preHouse.pay=0;
			}
			preHouse.community=PreDeal.stringFilter(house.community).trim();
			preHouse.area=house.area.replace("㎡", "").replace("平米", "").trim();
			
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
			preHouse.houseDetails=house.houseDetails.trim();
			preHouse.url=house.url.trim();
			preHouse.picNum=house.picNum;
			
			prehouseList.add(preHouse);
		}
	}
	
	//读入索引
	public  void initIndex(String tableName,LinkedHashMap<String, String> map){
		String sql="select * from "+tableName;
		ResultSet rSet=dBconnection.executeQuery(sql);
		try {
			while(rSet.next()){
				map.put(rSet.getString(2), rSet.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//融合
	public void merge(String tableName){
		System.out.println("融合前房源数:"+prehouseList.size());
		for(PreHouse preHouse:prehouseList){
			String communityIndex=this.communtiesIndex.get(preHouse.community);
			String locationIndex=this.locationsIndex.get(preHouse.location);
			String houseStyleIndex=this.houseStylesIndex.get(preHouse.houseStyle);
			String areaIndex=this.areasIndex.get(preHouse.area);
			String floorIndex=this.floorsIndex.get(preHouse.floor);
			
			String key=communityIndex+houseStyleIndex+areaIndex+floorIndex;
			
			if(!this.prehosuesMap.containsKey(key)){
				preHouse.communityID=key;
				this.prehosuesMap.put(key, preHouse);
				String ids="";
				ids=preHouse.source+"-"+String.valueOf(preHouse.id);
				this.mergeId.put(key, ids);
			}
			else {
				String ids=this.mergeId.get(key)+","+preHouse.source+"-"+String.valueOf(preHouse.id);
				this.mergeId.put(key, ids);
				int time1=Integer.parseInt(preHouse.crawlTime.replaceAll("-", ""));
				int time2=Integer.parseInt(this.prehosuesMap.get(key).crawlTime.replaceAll("-", ""));
				if(time1>time2){
					this.prehosuesMap.put(key, preHouse);
				}
				else {
					continue;
				}
			}
		}
		System.out.println("融合后房源数:"+this.prehosuesMap.size());
	}
	
	//统计小区
	public void CommunityInfo(String tableName){
		for(Entry<String, PreHouse> entry:this.prehosuesMap.entrySet()){
			String communityIndex=this.communtiesIndex.get(entry.getValue().community);
//			String locationIndex=this.locationsIndex.get(entry.getValue().location);
			
			String key=communityIndex;
			if(!this.communitiesMap.containsKey(key)){
				Community community=new Community();
				community.id=key;
				community.communityName=entry.getValue().community;
				community.communityLocation=entry.getValue().location;
				community.price=entry.getValue().pay;
				community.houseNum++;
				this.communitiesMap.put(key, community);
			}
			else {
				this.communitiesMap.get(key).houseNum++;
				int averagePrice=this.communitiesMap.get(key).price;
				int num=this.communitiesMap.get(key).houseNum;
				int price=(averagePrice*(num-1)+entry.getValue().pay)/num;
				this.communitiesMap.get(key).price=price;
			}
		}
	}
	
	public void storeHouse(String tableName){
		int count=0,total=this.prehosuesMap.size(),i;
		for(Entry<String, PreHouse> entry:this.prehosuesMap.entrySet()){
			String sql="insert IGNORE into"+" "+"test_"+tableName+"(id,来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,配置,联系人,联系方式,房源描述,图片数,编码,URL)"
					+" "+"values("+entry.getValue().id+","+"'"+entry.getValue().source+"'"+","+"'"+entry.getValue().title+"'"+","+"'"+entry.getValue().crawlTime+"'"+","
					+"'"+entry.getValue().time+"'"+","+entry.getValue().pay+","+"'"+entry.getValue().payStyle+"'"+","+"'"+entry.getValue().houseStyle+"'"+","
					+"'"+entry.getValue().rentStyle+"'"+","+"'"+entry.getValue().area+"'"+","+"'"+entry.getValue().style+"'"+","+"'"+entry.getValue().decoration+"'"+","
					+"'"+entry.getValue().direction+"'"+","+"'"+entry.getValue().floor+"'"+","+"'"+entry.getValue().community+"'"+","
					+"'"+entry.getValue().location+"'"+","+"'"+entry.getValue().installation+"'"+","+"'"+entry.getValue().contactMan+"'"+","+"'"+entry.getValue().contactWay+"'"+","
					+"'"+entry.getValue().houseDetails+"'"+","+entry.getValue().picNum+","+"'"+entry.getKey()+"'"+","+"'"+entry.getValue().url+"'"+")";
//			System.out.println(sql);
			i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				count++;
//				System.out.println(i+"条数据插入成功！");
			}
			else {
//				System.out.println("房源数据插入失败!");
//				System.out.println(sql);
			}
			
		}
		System.out.println("房源存储成功共有："+count);
		System.out.println("房源存储失败共有："+(total-count));
	}
	
	public void storeCommunity(String tableName){
		int count=0,total=this.communitiesMap.size(),i;
		for(Entry<String, Community> entry:this.communitiesMap.entrySet()){
			String sql="insert IGNORE into test_community_"+tableName+"(编号,小区,位置,房源数,平均价格) values("
		     +"'"+entry.getValue().id+"'"+","+"'"+entry.getValue().communityName+"'"+","
			 +"'"+entry.getValue().communityLocation+"'"+","+entry.getValue().houseNum+","
			 +entry.getValue().price+")";
//			System.out.println(sql);
			i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				count++;
//				System.out.println(i+"条数据插入成功！");
			}
			else {
//				System.out.println("小区数据插入失败!");
			}
			
		}
		System.out.println("小区存储成功共有："+count);
		System.out.println("小区存储失败共有："+(total-count));
	}
	
	public void storeMergeId(String tableName){
		int count=0,total=this.mergeId.size(),i;
		for(Entry<String, String> entry:this.mergeId.entrySet()){
			String sql="insert IGNORE into test_mergeId_"+tableName+"(编码,ids) values("
		     +"'"+entry.getKey()+"'"+","+"'"+entry.getValue()+"'"+")";
//			System.out.println(sql);
			i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				count++;
//				System.out.println(i+"条数据插入成功！");
			}
			else {
				System.out.println("房源编码插入失败!");
//				System.out.println(sql);
			}
			
		}
		System.out.println("融合的房源id成功共有："+count);
		System.out.println("融合的房源id失败共有："+(total-count));
	}

	
	//读取记录,简单过滤,多表
	public static void prehouseInfo(String tableName){
//		houseList.clear();
		houseList=RecordToHouse.recordToHouse(tableName);
		for(House house:houseList){
			PreHouse preHouse=new PreHouse();

			preHouse.pay=Integer.parseInt(house.pay.trim());

			preHouse.community=PreDeal.stringFilter(house.community).trim();
			preHouse.area=house.area.trim();
			
			preHouse.id=house.id;
			preHouse.title=house.title.trim();
			preHouse.crawlTime=house.crawlTime.trim();
			preHouse.time=house.time.trim();
			preHouse.source=house.source.trim();

			preHouse.payStyle=house.payStyle.trim();
			preHouse.houseStyle=house.houseStyle.trim();
			preHouse.rentStyle=house.rentStyle.trim();
			preHouse.style=house.style.trim();

			preHouse.decoration=house.decoration.trim();
			preHouse.direction=house.direction.trim();
			preHouse.floor=house.floor.trim();
			
			preHouse.location=house.location.trim();
			preHouse.installation=house.installation.trim();
			preHouse.contactMan=house.contactMan.trim();
			preHouse.contactWay=house.contactWay.trim();
			preHouse.houseDetails=house.houseDetails.trim();
			preHouse.url=house.url.trim();
			preHouse.picNum=house.picNum;
			
			prehouseList.add(preHouse);
		}
	}
	//3张表融合
	public void merge(String tableName1,String tableName2,String tableName3){
		this.infoClear();
		String tableName="merge";//存放数据，表名后缀
		prehouseInfo(tableName1);
		prehouseInfo(tableName2);
		prehouseInfo(tableName3);
		merge(tableName);
		CommunityInfo(tableName);
		storeHouse(tableName);
		storeMergeId(tableName);
		storeCommunity(tableName);
	}
	//2张表融合
	public void merge2(String tableName1,String tableName2){
		this.infoClear();
		String tableName="merge2";//存放数据，表名后缀
		prehouseInfo(tableName1);
		prehouseInfo(tableName2);
		merge(tableName);
		CommunityInfo(tableName);
		storeHouse(tableName);
		storeMergeId(tableName);
		storeCommunity(tableName);
	}
	
	public void infoClear(){
		houseList.clear();
		prehouseList.clear();
		prehosuesMap.clear();
	}
	
	//给房源编码
	public void bianma(){
		String tableName="pre_ganji";
		houseInfo(tableName);
		int count=0,total=this.prehouseList.size(),i;
		for(PreHouse preHouse:prehouseList){
			String communityIndex=this.communtiesIndex.get(preHouse.community);
			String locationIndex=this.locationsIndex.get(preHouse.location);
			String houseStyleIndex=this.houseStylesIndex.get(preHouse.houseStyle);
			String areaIndex=this.areasIndex.get(preHouse.area);
			String floorIndex=this.floorsIndex.get(preHouse.floor);
			
			String key=communityIndex+houseStyleIndex+areaIndex+floorIndex;
		
		
			String sql="insert IGNORE into"+" "+"bianma_"+tableName+"(id,来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,配置,联系人,联系方式,房源描述,图片数,编码,URL)"
					+" "+"values("+preHouse.id+","+"'"+preHouse.source+"'"+","+"'"+preHouse.title+"'"+","+"'"+preHouse.crawlTime+"'"+","
					+"'"+preHouse.time+"'"+","+preHouse.pay+","+"'"+preHouse.payStyle+"'"+","+"'"+preHouse.houseStyle+"'"+","
					+"'"+preHouse.rentStyle+"'"+","+"'"+preHouse.area+"'"+","+"'"+preHouse.style+"'"+","+"'"+preHouse.decoration+"'"+","
					+"'"+preHouse.direction+"'"+","+"'"+preHouse.floor+"'"+","+"'"+preHouse.community+"'"+","
					+"'"+preHouse.location+"'"+","+"'"+preHouse.installation+"'"+","+"'"+preHouse.contactMan+"'"+","+"'"+preHouse.contactWay+"'"+","
					+"'"+preHouse.houseDetails+"'"+","+preHouse.picNum+","+"'"+key+"'"+","+"'"+preHouse.url+"'"+")";
//			System.out.println(sql);
			i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				count++;
//				System.out.println(i+"条数据插入成功！");
			}
			else {
//				System.out.println("房源数据插入失败!");
				System.out.println(sql);
			}
			
		}
		System.out.println("编码成功共有："+count);
		System.out.println("编码失败共有："+(total-count));
	}
	
	public static void main(String[] args){
		MergeBaseline mb=new MergeBaseline();
//		mb.bianma();
		String[] tableName={"pre_ganji","pre_tongcheng","pre_anjuke"};//ganji,tongcheng,anjuke
		long start=System.currentTimeMillis();
		//表内融合
		for(int i=0;i<3;i++){
			System.out.println("--------------");
			System.out.println(tableName[i]);
			mb.infoClear();
			mb.houseInfo(tableName[i]);
			mb.merge(tableName[i]);
			mb.storeHouse(tableName[i]);
			mb.storeMergeId(tableName[i]);
			mb.CommunityInfo(tableName[i]);
			mb.storeCommunity(tableName[i]);
		}
		//3表融合
		System.out.println("------3表融合-------");
		mb.infoClear();
		mb.merge("test_pre_ganji", "test_pre_tongcheng", "test_pre_anjuke");
		//2表融合
//		mb.merge2("test_pre_3_ganji", "test_pre_3_tongcheng");
		long end=System.currentTimeMillis();
		long time=end-start;
 		MyTime.gettime(time);
//		House house=new House();
//		house.crawlTime="2015-01-02";
//		String crawlTime=house.crawlTime.replaceAll("-", "").replace("年", "").replace("月", "").replace("日", "");
//		System.out.println("crawlTime"+crawlTime);
		
	}
}
