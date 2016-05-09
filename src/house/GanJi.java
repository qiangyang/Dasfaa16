package house;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GanJi {
   public String source="ganji";
   public String title="";
   public String time="";
   public String pay="";
   public String payStyle="";
   public String houseStyle="";
   public String rentStyle="";
   public String area="";
   public String style="";
   public String decoration="";
   public String direction="";
   public String floor="";
   public String community="";
   public String location="";
   public String installation="";
   public String contactMan="";
   public String contactWay="";
   public String houseDetails="";
   public String url;
   public String crawlTime;
   public int picNum=0;
   public int pageviews=0;
   
   public void show(){
	   System.out.println(source+" "+" "+title+" "+crawlTime+" "+time+" "+pay+" "+houseStyle+" "+decoration
			   +" "+floor+" "+community+" "+location+" "+installation+" "+contactMan+" "+contactWay
			   +" "+houseDetails+" "+picNum+" "+url);
   }
   
   public static String getDate() {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	   return formatter.format(new Date());
   }
}
