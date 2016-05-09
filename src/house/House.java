package house;

import java.sql.ResultSet;
import java.sql.SQLException;

public class House {
	public int id;
	public String source="";
	public String title="";
	public String time="";
	public String pay="";
	public String payStyle="";
	public String houseStyle="";
	public String rentStyle="";
	public String style="";

	public String area="";
	public String decoration="";
	public String direction="";
	public String floor="";
	public String community="";
	public String location="";
	public String installation="";
	public String contactMan="";
	public String contactWay="";
	public String company="";
	public String subCompany="";
	public String houseDetails="";
	public String url="";
	public String crawlTime=""; 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPay() {
		return pay;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

	public String getPayStyle() {
		return payStyle;
	}

	public void setPayStyle(String payStyle) {
		this.payStyle = payStyle;
	}

	public String getHouseStyle() {
		return houseStyle;
	}

	public void setHouseStyle(String houseStyle) {
		this.houseStyle = houseStyle;
	}

	public String getRentStyle() {
		return rentStyle;
	}

	public void setRentStyle(String rentStyle) {
		this.rentStyle = rentStyle;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDecoration() {
		return decoration;
	}

	public void setDecoration(String decoration) {
		this.decoration = decoration;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getInstallation() {
		return installation;
	}

	public void setInstallation(String installation) {
		this.installation = installation;
	}

	public String getContactMan() {
		return contactMan;
	}

	public void setContactMan(String contactMan) {
		this.contactMan = contactMan;
	}

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSubCompany() {
		return subCompany;
	}

	public void setSubCompany(String subCompany) {
		this.subCompany = subCompany;
	}

	public String getHouseDetails() {
		return houseDetails;
	}

	public void setHouseDetails(String houseDetails) {
		this.houseDetails = houseDetails;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCrawlTime() {
		return crawlTime;
	}

	public void setCrawlTime(String crawlTime) {
		this.crawlTime = crawlTime;
	}

	public int getPicNum() {
		return picNum;
	}

	public void setPicNum(int picNum) {
		this.picNum = picNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int picNum=0;
	public String code = "";
	   
	public House(){
		   
	}
	   
	public House(ResultSet resultSet){
		try {
			this.id=resultSet.getInt("id");
			this.source=resultSet.getString("来源");
			this.title=resultSet.getString("标题");
			this.crawlTime=resultSet.getString("抓取时间");
			this.time=resultSet.getString("发布时间");
			this.pay=resultSet.getString("租金");
			this.payStyle=resultSet.getString("押付方式");
			this.houseStyle=resultSet.getString("户型");
			this.rentStyle=resultSet.getString("租凭方式");
			this.style=resultSet.getString("房屋类型");
			this.area=resultSet.getString("面积");
			this.decoration=resultSet.getString("装修");
			this.direction=resultSet.getString("朝向");
			this.floor=resultSet.getString("楼层");
			this.community=resultSet.getString("小区");
			this.location=resultSet.getString("位置");
			this.installation=resultSet.getString("配置");
			this.contactMan=resultSet.getString("联系人");
			this.contactWay=resultSet.getString("联系方式");
			this.houseDetails=resultSet.getString("房源描述");
			this.url=resultSet.getString("URL");
			this.picNum=resultSet.getInt("图片数");
			this.code= resultSet.getString("编码");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
