package com.edu.suda.housing.dbmap;

import java.io.File;
import java.util.HashMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

public class ReadDBMap {

	public DB mydb = null;
	String dbName="D:\\wordSegment\\words";
	
	HashMap<Integer, HashMap<String, Float>>  tfidf = null;
	HTreeMap<Integer, HashMap<String, Float>> db_map = null;
	
	public ReadDBMap() {
		// TODO Auto-generated constructor stub
		loadDB();
		inti();
	}
	
	public void inti(){
		tfidf = new HashMap<Integer,HashMap<String, Float>>();
		db_map = mydb.getHashMap("tfidf");
		
	}
	
	public void loadDB(){
		System.out.println("loadDB "+dbName+"begin");
		File dbFile1=new File(dbName);
		mydb=DBMaker.newFileDB(dbFile1)
				.transactionDisable()
				.closeOnJvmShutdown()
				.make();
		System.out.println("loadDB "+dbName+"end");	
	}
	
	public HashMap<Integer, HashMap<String, Float>> printdata(){
		for(Integer id: db_map.keySet()){
			//System.out.println(db_map.get(id));
			tfidf.put(id, db_map.get(id));
		}
		return tfidf;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ReadDBMap dbmap = new ReadDBMap(tfidf);
		
	}

}
