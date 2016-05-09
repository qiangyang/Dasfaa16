package com.edu.suda.housing.dbmap;

import java.io.File;
import java.util.HashMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

public class StoreDBMap {

	public DB mydb = null;
	String dbName="D:\\wordSegment\\words";
	
	HashMap<Integer, HashMap<String, Float>>  tfidf = null;
	HTreeMap<Integer, HashMap<String, Float>> db_map = null;
	public StoreDBMap() {
		// TODO Auto-generated constructor stub
		inti();
	}
	
	public void inti(){
		tfidf = new HashMap<Integer, HashMap<String, Float>>();
		createDB();
		db_map = mydb.getHashMap("tfidf");
		
	} 
	
	public void createDB(){
		//step 1:DB_AdjComponet_Name
		 File dbFile = new File(dbName);
	        if(dbFile.exists()){
	        	dbFile.delete();
	        }
	        mydb = DBMaker.newFileDB(dbFile)
	        		//.asyncWriteEnable()
	        		.transactionDisable()
	        		.freeSpaceReclaimQ(0)
	        		.asyncWriteEnable()
	        		//.cacheSize(1024*1024*100)
	        		//.cacheLRUEnable()
	        		//.cacheDisable()
	        		//.fullChunkAllocationEnable()
	        		//.mmapFileEnable()
	        		//.syncOnCommitDisable()
	        		.make();
	        System.out.println("Created BD:\t"+dbName);            
	}
	
	/*public void loaddata(){
		for(int i=0;i<100;i++)
		{
			if(!tfidf.containsKey(i+""))
			{
				//tfidf.put(i+"", i+"");
			}
		}
	}*/
	
	
	public void  savedata(HashMap<Integer, HashMap<String, Float>>  tfidf){
		db_map.putAll(tfidf);
		mydb.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
