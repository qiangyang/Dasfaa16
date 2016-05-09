package file;

public class MyTime {
	public static void gettime(long t){
    	int i;
    	String h,m,s,ss,time;
    	ss=String.valueOf(t%1000);
    	s=String.valueOf((t/1000)%60);
    	m=String.valueOf(((t/1000)/60)%60);
    	h=String.valueOf(((t/1000)/60)/60);
    	time=h+":"+m+":"+s+".";
    	i=ss.length();
    	while(i<3){
    		time+="0";
    		i++;
    	}
    	time+=ss;
    	System.out.println("运行时间："+time);
    }
}
