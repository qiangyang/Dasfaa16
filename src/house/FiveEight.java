package house;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zju.yuki.spider.fetcher.PageFetcher;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.parser.GanJiParser;

public class FiveEight {
	   public String source="58";
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
	   public String detailLocation="";
	   public String installation="";
	   public String contactMan="";
	   public String contactWay="";
	   public String houseDetails="";
	   public String url;
	   public String crawlTime;
	   public int picNum=0;
	   public void show(){
		   System.out.println(source+" "+" "+title+" "+crawlTime+" "+time+" "+pay+" "+decoration+" "+floor
				   +" "+community+" "+location+" "+installation+" "+contactMan+" "+contactWay
				   +" "+houseDetails+" "+" "+picNum+" "+url);
	   }
	   
	   public static String getDate() {
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		   return formatter.format(new Date());
	   }
	   
	   public static void main(String[] args){
			System.out.println(FiveEight.getDate());
		}
}
