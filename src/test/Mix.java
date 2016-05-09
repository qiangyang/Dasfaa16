package test;

import house.House;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import database.DBconnection;

public class Mix {
	private String tableName1;
	private String tableName2;
	private RecordToHouse rToHouse=new RecordToHouse();
	private ArrayList<House> housesList=new ArrayList<House>();
	private ReadFromDB rDb =new ReadFromDB();
	private LinkedHashMap<String, House> housesMap=new LinkedHashMap<String, House>();
	
	public void getHouses(String tableName){
		this.housesList.addAll(rToHouse.recordToHouse(tableName));
	}
	
	public void urlMix(String tableName){
		ArrayList<House> temp=new ArrayList<House>();
		for(House house:this.housesList){
			if(!this.housesMap.containsKey(house.url)){
				this.housesMap.put(house.url, house);
			}
			else {
				House existHouse=housesMap.get(house.url);
				if(house.crawlTime.compareTo(existHouse.crawlTime)>=0){
					this.housesMap.put(house.url, house);
				}
				else {
					continue;
				}
			}
		}
		for(Entry<String, House> entry:this.housesMap.entrySet()){
			temp.add(entry.getValue());
		}
		this.housesList.clear();
		this.housesList.addAll(temp);
	}
	
	public void innerMix(String tableName){
		ArrayList<Integer> mixIDList=new ArrayList<Integer>();//存放要融合的房源id
		for(int i=0;i<housesList.size();i++){
			if(mixIDList.contains(housesList.get(i).id)){
				continue;
			}
			else {
				for(int j=i+1;j<housesList.size();j++){
					
				}
			}
			
		}
		
	}
	
	public void getSimilarity(){
		for(int i=0;i<housesList.size();i++){
			
		}
	}
}
