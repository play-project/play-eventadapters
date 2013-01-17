/**
 * 
 */
package ningyuan.pan.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jdbm.DB;
import org.apache.jdbm.DBMaker;

import play.Logger;

/**
 * @author Ningyuan Pan
 *
 */
public class Persistence {
	
	//keep the opened DB connections
	private static Map<String, DB> openedDBs = new HashMap<String, DB>();
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	public Persistence(){
	}
	
	/**
	 * Get a connection to the database with name, for each database is only
	 * one connection kept open.
	 * @param name The location of DB file
	 * @return
	 */
	public static DB connect(String name){
		DB db;
		try{
			lock.lock();
			db = openedDBs.get(name);
			if(db == null || db.isClosed()){
				db = DBMaker.openFile(name)  
					.disableTransactions()
				    .enableEncryption("password",false)
				    .make();
				openedDBs.put(name, db);
			}
		}
		finally{
			lock.unlock();
		}
		return db;
	}
	
	/**
	 * Close the DB connection indicated by @param s.
	 * @param s The location of DB file
	 */
	public static void close(String s){
		try{
			lock.lock();
			DB db = openedDBs.get(s);
			if(db != null && !db.isClosed()){
				db.close();
				openedDBs.remove(s);
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	/**
	 * Close all the DB connections.
	 */
	public static void close(){
		try{
			lock.lock();
			for(String s : openedDBs.keySet()){
				DB db = openedDBs.get(s);
				if(!db.isClosed()){
					db.close();
				}
			}
			openedDBs.clear();
		}
		finally{
			lock.unlock();
				Logger.info("Persistance closed.");
		}
	}
}
