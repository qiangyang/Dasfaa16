package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.filechooser.FileNameExtensionFilter;



public class Myfile {
	/**
	 * 默认的文件
	 */
	private String defaultfilename="E:\\house";

	private LinkedHashMap<String, String> map1=new LinkedHashMap<String, String>();
	/**
	 * 输出的结果集
	 */
	private LinkedHashMap<String, Double> map=new LinkedHashMap<String, Double>();
	/**
	 * 存放读取的数据
	 */
	private ArrayList<String> list=new ArrayList<String>();
	/******************************************
	 * get\set
	 *****************************************/
	public String getfilename(){
		return this.defaultfilename;
	}
	public void setmap(HashMap<String, Double> result){
		this.map.clear();
		this.map.putAll(result);
	}
	public LinkedHashMap<String, Double> getmap(){
		return this.map;
	}
	public ArrayList<String> getlist(){
		return this.list;
	}
	/**
	 * 判断文件是否存在
	 * @method
	 */
	public boolean isHasFile(String filename){
		String filestr=this.defaultfilename+"\\"+filename;
		File file=new File(filestr);
		boolean flag=true;
		try {
			if(!file.exists()){
				flag=false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 * @method 创建文件
	 * @return boolean  成功flag=true  失败flag=false
	 */
	public boolean createfile(String filename)throws Exception{
		String filestr=this.defaultfilename+"\\"+filename;
		File file=new File(filestr);
		boolean flag=false;
		try {
			if(!file.exists()){
				file.createNewFile();
				flag=true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return flag;
	}
	
	public void readfile(String filename)throws Exception{
		String filestr=this.defaultfilename+"\\"+filename;
		File file=new File(filestr);
		FileReader fr=new FileReader(file);
		BufferedReader br=new BufferedReader(fr);
		try {
			String line;
			while((line=br.readLine())!=null){
				list.add(line);
			}
			System.out.println("数据读取成功！ 文件URL："+filestr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if(fr!=null){
				fr.close();
			}
			if(br!=null){
				br.close();
			}
		}
	}
	
	public void writefile(String filename)throws Exception{
		String filestr=this.defaultfilename+"\\"+filename;
		File file=new File(filestr);
		try{
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			if(!file.exists()){
				file.createNewFile();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		FileWriter fw=new FileWriter(file);
		try {
			for(String key:map.keySet()){
				String str=key+"="+map.get(key);
				fw.write(str+"\n");
			}
			System.out.println("数据写入成功！ 文件URL："+filestr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if(fw!=null){
				fw.close();
			}
		}
	}
	
	public void sort(){
		String temp;
		for(int i=0;i<list.size();i++){
			for(int j=i+1;j<list.size();j++){
				String[] array1=list.get(i).split(",");
			String[] array2=list.get(j).split(",");
			int a=Integer.parseInt(array1[0]);
			int b=Integer.parseInt(array2[0]);
			if(a>b){
				temp=list.get(i);
				list.set(i, list.get(j));
				list.set(j, temp);
			}
			}
			
		}
		for(int i=0;i<list.size();i++){
			String[] array=list.get(i).split(",");
			map1.put(array[0], array[1]);
		}
	}
//	public static void main(String[] args){
//		Table table=new Table("test_zol");
//		ArrayList<String> list=table.getcolumns().get(0);
//		Myfile mf=new Myfile();
//		String readfn="phone.csv";
//		String writefn="test.txt";
//		try {
//			mf.readfile(readfn);
//			mf.sort();
//			for(int i=0;i<list.size();i++){
//				if(mf.map.containsKey(list.get(i))){
//					mf.map1.put(list.get(i), mf.map.get(list.get(i)));
//				}
//			}
//			System.out.println(mf.map1);
//			mf.writefile(writefn);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
