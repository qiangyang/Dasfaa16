package DEMO;

import java.io.File;
import java.util.HashMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

public class MAPDB {

	
	DB ad;
	String dbName="D:\\DD";
	
	HashMap<String, String>  mappp;
	HTreeMap<String, String> db_mappp;
	
	public MAPDB()
	{
		inti();
	}
	
	public void inti()
	{
		mappp=new HashMap<String, String>();
		createDB();
		db_mappp=ad.getHashMap("yqdb");
		
	}
	
	
	
	public void loaddata()
	{
		
		for(int i=0;i<100;i++)
		{
			if(!mappp.containsKey(i+""))
			{
				mappp.put(i+"", i+"");
			}
		}
	}
	
	
	public void  savedata()
	{
		db_mappp.putAll(mappp);
		ad.close();
	}
	
	
	
	public void createDB()
	{
		//step 1:DB_AdjComponet_Name
		 File dbFile = new File(dbName);
	        if(dbFile.exists()){
	        	dbFile.delete();
	        }
	        ad = DBMaker.newFileDB(dbFile)
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MAPDB test=new MAPDB();
		test.loaddata();
		test.savedata();
	}

}
