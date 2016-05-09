package DEMO;

import java.io.File;
import java.util.HashMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

public class READDB {

	
	DB ad;
	String dbName="D:\\DD";
	
	HashMap<String, String>  mappp;
	HTreeMap<String, String> db_mappp;
	public READDB() {
		// TODO Auto-generated constructor stub\
		loadDB();
		inti();
		
	}
	public void inti()
	{
		mappp=new HashMap<String, String>();
//		createDB();
		db_mappp=ad.getHashMap("yqdb");
		
	}
	
	
	public void loadDB()
	{
		//Step 1:
		System.out.println("loadDB "+dbName+"begin");
//		watcher.start();
//		System.out.println();
		File dbFile1=new File(dbName);
		ad=DBMaker.newFileDB(dbFile1)
				.transactionDisable()
				.closeOnJvmShutdown()
				.make();
//		watcher.stop();
		
		System.out.println("loadDB "+dbName+"end");
//		watcher.seeRunTime();
		
					
		
	}
	
	void printdata()
	{
		for(int i=0;i<100;i++)
		{
			System.out.println(db_mappp.get(i+""));
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		READDB tst=new READDB();
		
		tst.printdata();
	}

}
