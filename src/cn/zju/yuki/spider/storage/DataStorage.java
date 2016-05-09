package cn.zju.yuki.spider.storage;

import database.DBconnection;
import house.AnJuKe;
import house.Community;
import house.FiveEight;
import house.GanJi;

public class DataStorage {
	
	private DBconnection dBconnection=DBconnection.getDbConnection();
	
	//ganji
	public void store(GanJi data){
		// store to DB
		// TODO
		try {
			String sql="insert into"+" "+data.source+"(来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,配置,联系人,联系方式,房源描述,图片数,URL)"
					+" "+"values("+"'"+data.source+"'"+","+"'"+data.title+"'"+","+"'"+data.crawlTime+"'"+","+"'"+data.time+"'"+","+"'"+data.pay+"'"+","
					+"'"+data.payStyle+"'"+","+"'"+data.houseStyle+"'"+","+"'"+data.rentStyle+"'"+","+"'"+data.area+"'"+","+"'"+data.style+"'"+","
					+"'"+data.decoration+"'"+","+"'"+data.direction+"'"+","+"'"+data.floor+"'"+","+"'"+data.community+"'"+","
					+"'"+data.location+"'"+","+"'"+data.installation+"'"+","+"'"+data.contactMan+"'"+","+"'"+data.contactWay+"'"+","
					+"'"+data.houseDetails+"'"+","+"'"+data.picNum+"'"+","+"'"+data.url+"'"+")";
			System.out.println(sql);
			int i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				System.out.println(i+"条数据插入成功！");
			}
			else {
				System.out.println("数据插入失败!");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("数据插入失败！");
			e.printStackTrace();
		}
	}
	//58
	public void store(FiveEight data){
		// store to DB
		// TODO
		try {
			String sql="insert into"+" "+"tongcheng"+"(来源,标题,抓取时间,发布时间,租金,押付方式,户型,租凭方式,面积,房屋类型,装修,朝向,楼层,小区,位置,详细地址,配置,联系人,联系方式,房源描述,图片数,URL)"
					+" "+"values("+"'"+data.source+"'"+","+"'"+data.title+"'"+","+"'"+data.crawlTime+"'"+","+"'"+data.time+"'"+","+"'"+data.pay+"'"+","
					+"'"+data.payStyle+"'"+","+"'"+data.houseStyle+"'"+","+"'"+data.rentStyle+"'"+","+"'"+data.area+"'"+","+"'"+data.style+"'"+","
					+"'"+data.decoration+"'"+","+"'"+data.direction+"'"+","+"'"+data.floor+"'"+","+"'"+data.community+"'"+","
					+"'"+data.location+"'"+","+"'"+data.detailLocation+"'"+","+"'"+data.installation+"'"+","+"'"+data.contactMan+"'"+","+"'"+data.contactWay+"'"+","
					+"'"+data.houseDetails+"'"+","+"'"+data.picNum+"'"+","+"'"+data.url+"'"+")";
			System.out.println(sql);
			int i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				System.out.println(i+"条数据插入成功！");
			}
			else {
				System.out.println("数据插入失败!");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("数据插入失败！");
			e.printStackTrace();
		}
	}
	//安居客
	public void store(AnJuKe data){
		// store to DB
		// TODO
		try {
			String sql="insert into"+" "+"anjuke"+"(来源,标题,房源编号,抓取时间,发布时间,租金,押付方式,户型,租凭方式,房屋类型,装修,面积,朝向,楼层,小区,位置,配置,联系人,联系方式,公司,店面,房源描述,图片数,室外图片数,URL)"
					+" "+"values("+"'"+data.source+"'"+","+"'"+data.title+"'"+","+"'"+data.houseNum+"'"+","+"'"+data.crawlTime+"'"+","
					+"'"+data.time+"'"+","+"'"+data.pay+"'"+","+"'"+data.payStyle+"'"+","+"'"+data.houseStyle+"'"+","+"'"+data.rentStyle+"'"+","
					+"'"+data.style+"'"+","+"'"+data.houseResume+"'"+","+"'"+data.area+"'"+","+"'"+data.direction+"'"+","+"'"+data.floor+"'"+","
					+"'"+data.community+"'"+","+"'"+data.location+"'"+","+"'"+data.installation+"'"+","+"'"+data.contactMan+"'"+","
					+"'"+data.contactWay+"'"+","+"'"+data.company+"'"+","+"'"+data.subCompany+"'"+","+"'"+data.houseDetails+"'"+","
					+"'"+data.picIn+"'"+","+"'"+data.picOut+"'"+","+"'"+data.url+"'"+")";
			System.out.println(sql);
			int i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				System.out.println(i+"条数据插入成功！");
			}
			else {
				System.out.println("数据插入失败!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("数据插入失败！");
			e.printStackTrace();
		}
	}
	
	public void store(Community data){
		// store to DB
		// TODO
		try {
			String sql="insert into"+" "+"community"+"(小区,区域,详细地址,竣工时间,物业类型,开发商,物业公司,二手房,出租房,URL)"
					+" "+"values("+"'"+data.communityName+"'"+","+"'"+data.communityLocation+"'"+","+"'"+data.detailLocation+"'"+","
					+"'"+data.finishTime+"'"+","
					+"'"+data.style+"'"+","+"'"+data.developer+"'"+","+"'"+data.Property+"'"+","+"'"+data.erhouseNum+"'"+","+"'"+data.houseNum+"'"
					+","+"'"+data.url+"'"+")";
			System.out.println(sql);
			int i=this.dBconnection.executeUpdate(sql);
			if(i>0){
				System.out.println(i+"条数据插入成功！");
			}
			else {
				System.out.println("数据插入失败!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("数据插入失败！");
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		AnJuKe ganJi=new AnJuKe();
		DataStorage storage=new DataStorage();
		storage.store(ganJi);
	}
}
