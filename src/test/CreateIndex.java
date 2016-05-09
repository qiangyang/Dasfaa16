package test;

import house.GanJi;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBconnection;

public class CreateIndex {
	private DBconnection dBconnection=DBconnection.getDbConnection();
	
	public void CommunityIndex(String tableName){
		int i=getMaxNum("communityIndex");
//		System.out.println(i);
		String sql="SELECT DISTINCT(小区) from"+" "+tableName+" GROUP BY 小区";
		ResultSet rSet=dBconnection.executeQuery(sql);
		try {
			int count=0;
			while(rSet.next()){
				int flag;
				String community=PreDeal.stringFilter(rSet.getString("小区"));
				if(!"".equals(community)&&null!=community){
					String id="";
					if(i>=1000){
						id=String.valueOf(i);
					}
					else if(i>=100){
						id="0"+i;
					}
					else if(i>=10){
						id="00"+i;
					}
					else {
						id="000"+i;
					}
					sql="Insert IGNORE Into CommunityIndex(communityIndex,community) values("+"'"+id+"'"+","+"'"+community+"'"+")";
					flag=dBconnection.executeUpdate(sql);
					i=i+flag;
					if(flag>0){
						count=count+flag;
					}
					
				}
			}
			System.out.println("CommunityIndex update:"+count);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void LocationIndex(String tableName){
		int i=getMaxNum("locationIndex");
//		System.out.println(i);
		String sql="SELECT DISTINCT(位置) from"+" "+tableName;
		ResultSet rSet=dBconnection.executeQuery(sql);
		try {
			int count=0;
			while(rSet.next()){
				int flag;
				String location=PreDeal.stringFilter(rSet.getString("位置").replace("苏州-", "").trim().replace(" ", "-"));
				if(!"".equals(location)&&null!=location){
					String id="";
					if(i>=1000){
						id=String.valueOf(i);
					}
					else if(i>=100){
						id="0"+i;
					}
					else if(i>=10){
						id="00"+i;
					}
					else {
						id="000"+i;
					}
					sql="Insert IGNORE Into locationIndex(locationIndex,location) values("+"'"+id+"'"+","+"'"+location+"'"+")";
					flag=dBconnection.executeUpdate(sql);
					i=i+flag;
					if(flag>0){
						count+=flag;
					}
				}
				
			}
			System.out.println("LocationIndex update:"+count);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void houseStyleIndex(String tableName){
//		int i=getMaxNum("houseStyleIndex");
//		System.out.println(i);
		String sql="SELECT DISTINCT(户型) from"+" "+tableName;
		ResultSet rSet=dBconnection.executeQuery(sql);
		try {
			int count=0;
			while(rSet.next()){
				int flag;
				String houseStyle=PreDeal.stringFilter(rSet.getString("户型")).trim();
				if(!"".equals(houseStyle)&&null!=houseStyle){
					String id=createHouseStyleIndex(houseStyle);

					sql="Insert IGNORE Into houseStyleIndex(houseStyleIndex,houseStyle) values("+"'"+id+"'"+","+"'"+houseStyle+"'"+")";
					flag=dBconnection.executeUpdate(sql);
					if(flag>0){
						count+=flag;
					}
				}
			}
			System.out.println("houseStyleIndex update:"+count);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void areaIndex(String tableName){
		String sql="SELECT DISTINCT(面积) from"+" "+tableName;
		ResultSet rSet=dBconnection.executeQuery(sql);
		try {
			int count=0;
			while(rSet.next()){
				int flag;
				String areasString=rSet.getString("面积").replace("㎡", "").replace("平米", "").trim();
				int index=areasString.indexOf(".");
				if(index>0){
					areasString=areasString.substring(0, index).trim();
				}
				if(!"".equals(areasString)&&null!=areasString){
					double area=Double.parseDouble(areasString.trim());
//					System.out.println(area);
					sql="Insert IGNORE Into areaIndex(areaIndex,area) values("+area+","+"'"+areasString+"'"+")";
					flag=dBconnection.executeUpdate(sql);
					if(flag>0){
						count+=flag;
					}
				}
			}
			System.out.println("areaIndex update:"+count);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void floorIndex(String tableName){
		int i=getMaxNum("floorIndex");
//		System.out.println(i);
		String sql="SELECT DISTINCT(楼层) from"+" "+tableName;
		ResultSet rSet=dBconnection.executeQuery(sql);
		try {
			int count=0;
			while(rSet.next()){
				int flag;
				String floor=rSet.getString("楼层").replace("层", "").trim();
				if(!"".equals(floor)&&null!=floor){
					String[] array=floor.split("/");
					String id="";
					if(array[0].length()==2){
						id+=array[0];
					}
					else if(array[0].length()==1){
						id+="0"+array[0];
					}
					if(array[1].length()==2){
						id+=array[1];
					}
					else if(array[1].length()==1){
						id+="0"+array[1];
					}
					sql="Insert IGNORE Into floorIndex(floorIndex,floor) values("+"'"+id+"'"+","+"'"+floor+"'"+")";
					flag=dBconnection.executeUpdate(sql);
//					i=i+flag;
					if(flag>0){
						count+=flag;
					}
				}
			}
			System.out.println("floorIndex update:"+count);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getMaxNum(String tableName){
		String sql="SELECT MAX("+tableName+") from "+tableName;
		ResultSet rSetNum=dBconnection.executeQuery(sql);
		int i=0;
		try {
			if(rSetNum.next()){
				i=rSetNum.getInt(1);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return i;
	}
	
	public String createHouseStyleIndex(String houseStyle){
		String id1="00",id2="00",id3="00";
		String id="";
		int index1,index2,index3;
		index1=houseStyle.indexOf("室");
		index2=houseStyle.indexOf("厅");
		index3=houseStyle.indexOf("卫");
		if(index1>0&&index2>0&&index3>0){
			id1=houseStyle.substring(0, index1);
			id2=houseStyle.substring(index1+1, index2);
			id3=houseStyle.substring(index2+1, index3);
		}
		else if(index1>0&&index2>0&&index3<0){
			id1=houseStyle.substring(0, index1);
			id2=houseStyle.substring(index1+1, index2);
		}
		else if(index1>0&&index2<0&&index3>0){
			id1=houseStyle.substring(0, index1);
			id3=houseStyle.substring(index1+1, index3);
		}
		else if(index1<0&&index2>0&&index3>0){
			id2=houseStyle.substring(0, index2);
			id3=houseStyle.substring(index2+1, index3);
		}
		else if(index1<0&&index2<0&&index3>0){
			id3=houseStyle.substring(0, index3);
		}
		else if(index1<0&&index2>0&&index3<0){
			id2=houseStyle.substring(0, index2);
		}
		else if(index1>0&&index2<0&&index3<0){
			id1=houseStyle.substring(0, index1);
		}
		id1=CreateIndex.format(id1);
		id2=CreateIndex.format(id2);
		id3=CreateIndex.format(id3);
		id=id1+id2+id3;
		return id;
	}
	
	public static String format(String str){
		if(str.length()==1){
			str="0"+str;
		}
		return str;
	}
	
	public static void main(String[] args){
		CreateIndex cIndex=new CreateIndex();
//		String tableName="pre_3_ganji";//ganji,tongcheng,anjuke
		String[] tableName={"pre_ganji","pre_tongcheng","pre_anjuke"};
		for(int i=0;i<3;i++){
			System.out.println("-----------------");
			System.out.println("create index:"+tableName[i]);
			cIndex.CommunityIndex(tableName[i]);
			cIndex.LocationIndex(tableName[i]);
//			cIndex.houseStyleIndex(tableName[i]);
//			cIndex.areaIndex(tableName[i]);
//			cIndex.floorIndex(tableName[i]);
		}
//		String houseStyle="2卫";
//		System.out.println(cIndex.createHouseStyleIndex(houseStyle));
	}
}
