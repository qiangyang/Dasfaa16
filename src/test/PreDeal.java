package test;

import house.Community;
import house.House;
import house.PreHouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import database.DBconnection;

public class PreDeal {
	private DBconnection dBconnection=DBconnection.getDbConnection();
	
	private String tableName=null;
	
	private static ArrayList<House> houseList=new ArrayList<House>();
	
	public PreDeal(String tableName){
		this.tableName=tableName;
	}
	
//	public void dealCommunity(){
//		String sql="INSERT INTO pre_community_"+this.tableName+"(小区,位置,房源数) SELECT 小区,位置,count(小区) from"+" "+this.tableName+" GROUP BY 小区,位置";
//		dBconnection.executeUpdate(sql);
//	}
	
	public void dealDetails(){
		String sql="select * from"+" "+this.tableName;
		ResultSet rs=dBconnection.executeQuery(sql);
		try {
			String temp;
			int index;
			while(rs.next()){
				temp=rs.getString("租金");
				index=temp.indexOf("/");
				String pay="";
				String payStyle="";
				if(index>0){
					pay=temp.substring(0, index+2).trim();
				}
				index=temp.indexOf("押");
				if(index>0){
					payStyle=temp.substring(index).replace(")", "").trim();
				}
				temp=rs.getString("户型");
				index=temp.indexOf("卫");
				String houseStyle="";
				String rentStyle="";
				String area="";
				if(index>0){
					houseStyle=temp.substring(0, index+1);
					String[] array=temp.split("-");
					rentStyle=array[1];
					area=array[2];
				}
				else {
					String[] array=temp.split("-");
					rentStyle=array[0];
					area=array[1];
				}
				String direction="";
				String style="";
				String zhuangxiu="";
				temp=rs.getString("房屋概况");
				if(temp.indexOf("-")>0){
					String[] array=temp.split("-");
					for(String str:array){
						if(str.contains("朝")){
							direction=str.trim();
						}
						else if(str.contains("修")||str.contains("毛坯")){
							zhuangxiu=str.trim();
						}
						else {
							style=str.trim();
						}
					}
				}
//				String idString="";
//				String sqlString="select 小区编号 from pre_community_"+this.tableName+" "+"where 小区="+"'"+rs.getString("小区")+"'";
//				ResultSet rSet=dBconnection.executeQuery(sqlString);
//				if(rSet.next()){
//					idString=rSet.getString("小区编号");
//				}
//				int cid=Integer.parseInt(idString);
//				System.out.println(pay+","+payStyle+","+rentStyle+";"+area+";"+direction+";"+style+";"+zhuangxiu+";"+cid);
				String source="ganji";
				String title="";
				String time="";
				String floor="";
				String community="";
				String location="";
				String installation="";
				String contactMan="";
				String contactWay="";
				String houseDetails="";
				String url;
				String crawlTime;
				int picNum=0;
				int pageviews=0;
				int id;
				source=rs.getString("来源").trim();
				title=rs.getString("标题").trim();
				crawlTime=rs.getString("抓取时间").trim();
				time=rs.getString("发布时间").trim();
				floor=rs.getString("楼层").trim();
				community=rs.getString("小区").replace(" ", "").trim();
				location=rs.getString("位置").replace(" ", "").trim();
				installation=rs.getString("配置").trim();
				contactMan=rs.getString("联系人").trim();
				contactWay=rs.getString("联系方式").trim();
				houseDetails=rs.getString("房源描述").trim();
				url=rs.getString("URL").trim();
				picNum=rs.getInt("图片数");
				id=rs.getInt("id");
				String sqlStr="insert into"+" "+"ganji"+"(id,来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,配置,联系人,联系方式,房源描述,图片数,URL)"
						+" "+"values("+id+","+"'"+source+"'"+","+"'"+title+"'"+","+"'"+crawlTime+"'"+","+"'"+time+"'"+","+"'"+pay+"'"+","
						+"'"+payStyle+"'"+","+"'"+houseStyle+"'"+","+"'"+rentStyle+"'"+","+"'"+area+"'"+","+"'"+style+"'"+","+"'"+zhuangxiu+"'"+","
						+"'"+direction+"'"+","+"'"+floor+"'"+","+"'"+community+"'"+","
						+"'"+location+"'"+","+"'"+installation+"'"+","+"'"+contactMan+"'"+","+"'"+contactWay+"'"+","
						+"'"+houseDetails+"'"+","+picNum+","+"'"+url+"'"+")";
				System.out.println(sqlStr);
				if(this.dBconnection.executeUpdate(sqlStr)>0){
					System.out.println("数据插入成功！");
				}
				else {
					System.out.println("数据插入失败!");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e);
		}
	}
	
	public void dealDetails2(String tableName){
		String sql="select * from"+" "+tableName;
		ResultSet rs=dBconnection.executeQuery(sql);
		try {
			String temp;
			int index;
			while(rs.next()){
				temp=rs.getString("租金");
				index=temp.indexOf("/");
				String pay="";
				String payStyle="";
				if(index>0){
					pay=temp.substring(0, index+2).trim();
				}
				index=temp.indexOf("押");
				if(index>0){
					payStyle=temp.substring(index).replace(")", "").trim();
				}
				
				
				temp=PreDeal.stringFilter(rs.getString("房屋概况"));
				index=temp.indexOf("卫");
				String houseStyle="";
				String rentStyle="";
				String area="";
				String direction="";
				String style="";
				String zhuangxiu="";
				if(index>0){
					houseStyle=temp.substring(0, index+1);
					int areaindex=temp.indexOf("㎡");
					if(areaindex>0){
						area=temp.substring(index+1, areaindex+1);
						if(temp.contains("普通住宅")){
							int i=temp.indexOf("宅");
							style=temp.substring(areaindex+1, i+1);
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(i+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
						else if(temp.contains("公寓")){
							int i=temp.indexOf("寓");
							style=temp.substring(areaindex+1, i+1);
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(i+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
						else if(temp.contains("商住两用")){
							int i=temp.indexOf("用");
							style=temp.substring(areaindex+1, i+1);
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(i+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
						else {
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(areaindex+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
					}
					else {
						area="";
						if(temp.contains("普通住宅")){
							int i=temp.indexOf("宅");
							style=temp.substring(areaindex+1, i+1);
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(i+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
						else if(temp.contains("公寓")){
							int i=temp.indexOf("寓");
							style=temp.substring(areaindex+1, i+1);
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(i+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
						else if(temp.contains("商住两用")){
							int i=temp.indexOf("用");
							style=temp.substring(areaindex+1, i+1);
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(i+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
						else {
							if(temp.contains("装修")){
								int index2=temp.indexOf("修");
								zhuangxiu=temp.substring(index+1, index2+1);
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
							else {
								if(temp.contains("朝")){
									int index3=temp.indexOf("朝");
									direction=temp.substring(index3);
								}
							}
						}
					}
				}
				else {
					int index1=temp.indexOf("厅");
					if(index1>0){
						houseStyle=temp.substring(0, index1+1);
						int areaindex=temp.indexOf("㎡");
						if(areaindex>0){
							area=temp.substring(index1+1, areaindex+1);
							if(temp.contains("普通住宅")){
								int i=temp.indexOf("宅");
								style=temp.substring(areaindex+1, i+1);
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(i+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
							else if(temp.contains("公寓")){
								int i=temp.indexOf("寓");
								style=temp.substring(areaindex+1, i+1);
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(i+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
							else if(temp.contains("商住两用")){
								int i=temp.indexOf("用");
								style=temp.substring(areaindex+1, i+1);
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(i+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
							else {
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(areaindex+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
						}
						else {
							area="";
							if(temp.contains("普通住宅")){
								int i=temp.indexOf("宅");
								style=temp.substring(areaindex+1, i+1);
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(i+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
							else if(temp.contains("公寓")){
								int i=temp.indexOf("寓");
								style=temp.substring(areaindex+1, i+1);
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(i+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
							else if(temp.contains("商住两用")){
								int i=temp.indexOf("用");
								style=temp.substring(areaindex+1, i+1);
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(i+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
							else {
								if(temp.contains("装修")){
									int index2=temp.indexOf("修");
									zhuangxiu=temp.substring(index+1, index2+1);
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
								else {
									if(temp.contains("朝")){
										int index3=temp.indexOf("朝");
										direction=temp.substring(index3);
									}
								}
							}
						}
					}
					else {
						int index2=temp.indexOf("室");
						if(index2>0){
							houseStyle=temp.substring(0, index2+1);
							int areaindex=temp.indexOf("㎡");
							if(areaindex>0){
								area=temp.substring(index2+1, areaindex+1);
								if(temp.contains("普通住宅")){
									int i=temp.indexOf("宅");
									style=temp.substring(areaindex+1, i+1);
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(i+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
								else if(temp.contains("公寓")){
									int i=temp.indexOf("寓");
									style=temp.substring(areaindex+1, i+1);
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(i+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
								else if(temp.contains("商住两用")){
									int i=temp.indexOf("用");
									style=temp.substring(areaindex+1, i+1);
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(i+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
								else {
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(areaindex+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
							}
							else {
								area="";
								if(temp.contains("普通住宅")){
									int i=temp.indexOf("宅");
									style=temp.substring(areaindex+1, i+1);
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(i+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
								else if(temp.contains("公寓")){
									int i=temp.indexOf("寓");
									style=temp.substring(areaindex+1, i+1);
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(i+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
								else if(temp.contains("商住两用")){
									int i=temp.indexOf("用");
									style=temp.substring(areaindex+1, i+1);
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(i+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
								else {
									if(temp.contains("装修")){
										int index4=temp.indexOf("修");
										zhuangxiu=temp.substring(index+1, index4+1);
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
									else {
										if(temp.contains("朝")){
											int index3=temp.indexOf("朝");
											direction=temp.substring(index3);
										}
									}
								}
							}
						}
					}
				}
				String source="58";
				String title="";
				String time="";
				String floor="";
				String community="";
				String location="";
				String installation="";
				String contactMan="";
				String contactWay="";
				String houseDetails="";
				String url;
				String crawlTime;
				int picNum=0;
				int pageviews=0;
				int id;
				source=rs.getString("来源");
				title=rs.getString("标题");
				crawlTime=rs.getString("抓取时间");
				time=rs.getString("发布时间");
				floor=rs.getString("楼层");
				community=rs.getString("小区");
				location=rs.getString("位置");
				installation=rs.getString("配置");
				contactMan=rs.getString("联系人");
				contactWay=rs.getString("联系方式");
				houseDetails=rs.getString("房源描述");
				url=rs.getString("URL");
				picNum=rs.getInt("图片数");
				id=rs.getInt("id");
				String sqlStr="insert into"+" "+"tongcheng"+"(id,来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,配置,联系人,联系方式,房源描述,图片数,URL)"
						+" "+"values("+id+","+"'"+source+"'"+","+"'"+title+"'"+","+"'"+crawlTime+"'"+","+"'"+time+"'"+","+"'"+pay+"'"+","
						+"'"+payStyle+"'"+","+"'"+houseStyle+"'"+","+"'"+rentStyle+"'"+","+"'"+area+"'"+","+"'"+style+"'"+","+"'"+zhuangxiu+"'"+","
						+"'"+direction+"'"+","+"'"+floor+"'"+","+"'"+community+"'"+","
						+"'"+location+"'"+","+"'"+installation+"'"+","+"'"+contactMan+"'"+","+"'"+contactWay+"'"+","
						+"'"+houseDetails+"'"+","+picNum+","+"'"+url+"'"+")";
				System.out.println(sqlStr);
				if(this.dBconnection.executeUpdate(sqlStr)>0){
					System.out.println("数据插入成功！");
				}
				else {
					System.out.println("数据插入失败!");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e);
		}
	}
	
	
	//过滤特殊字符
	public static String stringFilter(String str) throws PatternSyntaxException{
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？·@&?]";
//		String regEx="[^u4e00-u9fa5]";		
		Pattern pattern=Pattern.compile(regEx);
		Matcher matcher=pattern.matcher(str);
		return matcher.replaceAll("").trim();
	}
	
	public static void houseInfo(String tableName){
		houseList.clear();
		houseList=RecordToHouse.recordToHouse(tableName);
		for(House house:houseList){
			PreHouse preHouse=new PreHouse();
			if(house.pay.contains("月")){
				int index=house.pay.indexOf("元");
				preHouse.pay=Integer.parseInt(house.pay.substring(0,index));
			}
			else {
				int index=house.pay.indexOf("元");
				preHouse.pay=Integer.parseInt(house.pay.substring(0, index))/12;
			}
			house.community=stringFilter(house.community);
		}
	}
	
	public static void communityInfo(String tableName){
		ArrayList<Community> communitiesList=new ArrayList<Community>();
		ArrayList<House> houseList=new ArrayList<House>();
		houseList=RecordToHouse.recordToHouse(tableName);
		LinkedHashMap<String, ArrayList<House>> houseMap=new LinkedHashMap<String, ArrayList<House>>();
		for(House house:houseList){
			String key=house.community+","+house.location;
			if(!houseMap.containsKey(key)){
				ArrayList<House> tempList=new ArrayList<House>();
				tempList.add(house);
				houseMap.put(key, tempList);
			}
			else {
				houseMap.get(key).add(house);
			}
		}
		for(Entry<String, ArrayList<House>> entry:houseMap.entrySet()){
			
		}
	}
	//未写完
	public static void eachHouse(House house){
		PreHouse preHouse=new PreHouse();
		if(house.pay.contains("月")){
			int index=house.pay.indexOf("元");
			preHouse.pay=Integer.parseInt(house.pay.substring(index));
		}
		else if(house.pay.contains("年")){
			int index=house.pay.indexOf("元");
			preHouse.pay=Integer.parseInt(house.pay.substring(index))/12;
		}
		else {
			preHouse.pay=0;//0代表面议
		}
		
		preHouse.area=house.area.replace("㎡", "").replace("平米", "").trim();//可以转为double
		
		preHouse.community=stringFilter(house.community);
		
		preHouse.id=house.id;
		preHouse.title=house.title;
		preHouse.crawlTime=house.crawlTime;
		preHouse.time=house.time;
		preHouse.source=house.source;

		preHouse.payStyle=house.payStyle;
		preHouse.houseStyle=house.houseStyle;
		preHouse.rentStyle=house.rentStyle;
		preHouse.style=house.style;

		preHouse.decoration=house.decoration;
		preHouse.direction=house.direction;
		preHouse.floor=house.floor;
		preHouse.community=house.community;
		preHouse.location=house.location;
		preHouse.installation=house.installation;
		preHouse.contactMan=house.contactMan;
		preHouse.contactWay=house.contactWay;
		preHouse.houseDetails=house.houseDetails;
		preHouse.url=house.url;
		preHouse.picNum=house.picNum;
	}
	
	
	public static void main(String[] args){
		PreDeal p=new PreDeal("2_ganji");
//		p.dealCommunity();
//		p.dealDetails();
		p.dealDetails2("2_tongcheng");
//		System.out.println("你好");
//		String   str   =   "*adCVs*34_a _09_b5*[/435^*&城池()^$$&*).{}+.|.)%%*(*.中国}34{45[]12.fd'*&999下面是中文的字符￥……{}【】。，；’“‘”？";  
//		String str="捷达明轩 ";
//        System.out.println(str);
//        System.out.println(str.trim());
//        str=stringFilter(str);
//        System.out.println(str);
//		String str="苏州 - 昆山 - 玉山城东".replace(" ", "").trim();
//		System.out.println(str);
	}
	
}
