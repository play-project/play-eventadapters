package ningyuan.pan.eventstream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import ningyuan.pan.util.Buffer;
import ningyuan.pan.util.Persistence;

import org.ontoware.rdf2go.model.Model;

import play.Logger;
import eventcloud.CustomEventNotificationListener;
import fr.inria.eventcloud.adapters.rdf2go.SubscribeRdf2goAdapter;
import fr.inria.eventcloud.api.SubscribeApi;
import fr.inria.eventcloud.api.Subscription;
import fr.inria.eventcloud.api.SubscriptionId;
import fr.inria.eventcloud.api.listeners.NotificationListener;
import fr.inria.eventcloud.factories.NotificationListenerFactory;

/**
 * @author Ningyuan Pan
 *
 */

public class EventStreamManager{
	
	// when subscribed successfully set true, when unsubscribed successfully
	// set false
	private volatile boolean connected = false;
	
	// all http-streams that are listening to this event stream
	private List<HTTPStreamObserver> observers = new ArrayList<HTTPStreamObserver>();
	
	// main lock 
	private final ReentrantLock lock = new ReentrantLock();
	
	// keep subscription id of this event stream and save to database lately
	// better be manipulated in one thread or make sure the process is thread safe
	private Set<SubscriptionId> subbedIds;
	
	private SubscriptionId subID;
	
	// query that is subscribed
	private String query;
	
	// the name of event stream
	private final String streamId;
	
	// subscrib proxy
    private final SubscribeRdf2goAdapter subscribeProxy;
    
	// listener to receive the content of event stream from event cloud
	private final EventStreamListener listener;
	
	private final EventRDFSyntaxTranslator translator = new EventRDFSyntaxTranslator();
	
	// buffer for events coming from event cloud
	private final Buffer<Model> buffer = new Buffer<Model>(1024);
	
	private FetchWorker worker = new FetchWorker();
	// the event fetching thread from buffer
	private Thread fetchThread = new Thread(worker);
	 
	public EventStreamManager(SubscribeRdf2goAdapter p, String q, String n, String db){
		subscribeProxy = p;
		query = q;
		streamId = new String(n);
		listener = NotificationListenerFactory.newNotificationListener(
                EventStreamListener.class,
                new Object[] {streamId});
		
		clearUnsubcribeCache(db);
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	public String getQueryContent(){
		return query;
	}
	
	/**
	 * 
	 * @param c
	 */
	public void setQueryContent(String c){
		if(c == null)
			throw new IllegalArgumentException();
		else
			query = c;
	}
	
	public void notifyObserver(Model event){
		EventRDFSyntaxTranslator trans = new EventRDFSyntaxTranslator(event); 
		for(HTTPStreamObserver<EventRDFSyntaxTranslator> o : observers){
			o.update(trans);
		}
	}
	
	/**
	 * Add a HTTP observer. If the observer is the first arriving one,
	 * or an unsubscription is made before, then make subscription.   
	 * @param o
	 */
	public void addObserver(HTTPStreamObserver o){
		try{
			lock.lock();
				Logger.info("Add HTTPStream: "+streamId);
				
		    // when the first observer comes or is still not subscribed
			if(observers.isEmpty() || connected == false){
				observers.add(o);
				subscribe();
			}
			else{
				observers.add(o);
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	/**
	 * Remove a HTTP observer. If the observer is the last one and 
	 * no unsubscription is made before, then make unsubscription.
	 * @param o
	 */
	public void removeObserver(HTTPStreamObserver o){
		try{
			lock.lock();
			observers.remove(o);
				Logger.info("Remove HTTPStream");
		    // when there is no observer for this event stream and is not subscribed
			if(observers.isEmpty() && connected == true){
				unsubscribe();
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	/**
	 * Disconnect the connection to the event cloud.
	 */
	public void disconnect(){
		try{
			lock.lock();
			if(connected == true)
				unsubscribe();
		}
		finally{
			lock.unlock();
		}
	}
	
	/*
	 * *************************************************************************
	 */
	public void bufferEvent(Model event){
		try {
			if(connected)
				buffer.add(event);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// only can be called before subscription
	private void startFetchingThread(){
		buffer.clear();
		fetchThread.start();
	}
	
	
	private void interruptFetchingThread(){
		fetchThread.interrupt();
	}
	
	/*
	 * *******************************************************************************
	 */
	
	private void clearUnsubcribeCache(String db){
		// the subbedIds must be created before 
		subbedIds = Persistence.connect(db).getHashSet(streamId);
		for(SubscriptionId sid : subbedIds){
			subscribeProxy.unsubscribe(sid);
			Logger.info(streamId+" clear unsubbed cache: "+sid.toString());
		}
		subbedIds.clear();
	}
	
	// must be called when holding the lock, to protect variable connected
	private void subscribe(){
		//TODO sequence of setting, sub and start thread
		// connected can be set true only here, when subscribed.
		Subscription sub = new Subscription(query);
		try{
			subscribeProxy.subscribe(sub, listener);
			connected = true;
		}
		catch(RuntimeException re){
			re.printStackTrace();
			Logger.info("Eventmanager.sub():run time Exception");
		}
		//startFetchingThread();
		subID= sub.getId();
		subbedIds.add(subID);
		
		Logger.info("Subscribed: "+subID.toString());
	}
	
	// must be called when holding the lock, to protect variable connected
	private void unsubscribe(){
		//TODO sequence of setting, sub and start thread
		// connected can be set false only here, when unsubscribed.
		try{
			Logger.info("Unsubscribed : "+subID.toString());
			subscribeProxy.unsubscribe(subID);
			Logger.info("Unsubscribed succesfully: "+subID.toString());
			connected = false;
		}
		catch(RuntimeException re){
			re.printStackTrace();
			Logger.info("Eventmanager.unsub():run time Exception");
		}
		//interruptFetchingThread();
		subbedIds.remove(subID);

	}	
	
	/*
	 * inner class for event fetching (not used)
	 */
	private class FetchWorker implements Runnable{
		volatile boolean started = false;
		
		@Override
		public void run() {
			started = true;
			while(connected){
				try {
					Model event = buffer.get();
						Logger.info("Fetching Thread :"+buffer.getSize()+"  "+Thread.currentThread().getId());
					translator.setNextEvent(event);
					for(HTTPStreamObserver<EventRDFSyntaxTranslator> o : observers){
						o.update(translator);
					}
				} catch (InterruptedException e) {
					break;
				}
			}
			started = false;
			Logger.info("Fetching Thread stoped");
		}
		
		boolean isStarted(){
			return started;
		}
	}
}
