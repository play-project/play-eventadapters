/**
 * 
 */
package ningyuan.pan.eventstream;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ningyuan Pan
 *
 */
public class ManagerRegistry {
	
	private static ManagerRegistry instance;
	
	private Map<String, EventStreamManager> managers;
	private final ReentrantLock lock = new ReentrantLock();
	
	private ManagerRegistry(){
		managers = new HashMap<String, EventStreamManager>();
	}
	
	public static ManagerRegistry getInstance(){
		if(instance == null)
			instance = new ManagerRegistry();
		return instance;
	}
	
	/**
	 * 
	 * @param s
	 * @param m
	 * @return The previous value associated with the key, could be null
	 */
	public EventStreamManager setManager(String s, EventStreamManager m){
		try{
			lock.lock();
			if(m == null)
				throw new IllegalArgumentException();
			return managers.put(s, m);
		}
		finally{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public EventStreamManager getManager(String s){
		return managers.get(s);
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public boolean contains(String s){
		return managers.containsKey(s);
	}
	
	/**
	 * Call this method, before the application is shutdown
	 * to make sure that all registered event manager is disconnected with event cloud.
	 */
	public void close(){
		try{
			lock.lock();
			for(String key : managers.keySet()){
				managers.get(key).disconnect();
			}
		}
		finally{
			lock.unlock();
		}
	}
}
