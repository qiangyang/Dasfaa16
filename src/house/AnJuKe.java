package house;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AnJuKe {
	   public static String source="anjuke";
	   public String title="";
	   public String time="";
	   public String pay="";
	   public String payStyle="";
	   public String houseStyle="";
	   public String rentStyle="";
	   public String style="";
	   public String houseResume="";
	   public String area="";
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
	   
	   public String houseNum="";
	   public String picIn="0";
	   public String picOut="0";
	   
	   public void show(){
		   System.out.println(source+" "+" "+title+" "+houseNum+" "+crawlTime+" "+time+" "+pay+" "+payStyle+" "+houseStyle+" "
	               +rentStyle+" "+style+" "+houseResume+" "+area+" "+direction+" "+floor+" "+community+" "+location+" "
				   +installation+" "+contactMan+" "+contactWay+" "+company+" "+subCompany+" "+houseDetails+" "+picIn+" "+picOut+" "+url);
	   }
	   
	   public static String getDate() {
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		   return formatter.format(new Date());
	   }
	   
	   public static void main(String[] args){
		   AnJuKe anJuKe=new AnJuKe();
		   anJuKe.show();
		}
}
