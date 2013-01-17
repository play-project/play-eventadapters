/**
 * 
 */
package ningyuan.pan.eventstream;

import org.ontoware.rdf2go.model.Model;

import play.Logger;
import fr.inria.eventcloud.adapters.rdf2go.listeners.Rdf2GoCompoundEventNotificationListener;
import fr.inria.eventcloud.api.SubscriptionId;

/**
 * @author Ningyuan Pan
 *
 */
public class EventStreamListener extends Rdf2GoCompoundEventNotificationListener {
	
	private static final long serialVersionUID = 1L;
	
	private String streamID;
	private volatile boolean valid = true;
	
	//private transient EventStreamManager manager;
	public EventStreamListener(){
		
	}
	
	/*public EventStreamListener(EventStreamManager ma){
		manager = ma;
	}*/
	
	public EventStreamListener(String s){
		streamID = s;
		
			Logger.info("Orig Lis Thread: "+Thread.currentThread().getId()+"  "+this.hashCode());
	}
	
	public void disable(){
		valid = false;
	}
	
	@Override
	public void handle(SubscriptionId arg0, Model event) {
		// !!!! This method can be called by different threads at the same time !!!! 
		// !!!! And "this" object in these threads is different with the initial one created by constructor !!!!
		//Logger.info("onNotification stream name: "+streamName);
		//if(valid){
		try{
			Logger.info("Eve Lis Thread: "+this.hashCode());
			EventStreamManager m = ManagerRegistry.getInstance().getManager(streamID);
			m.notifyObserver(event);
			//manager.notifyObserver(event);
			//m.bufferEvent(event);	
		}
		catch(RuntimeException re){
			re.printStackTrace();
			Logger.info("EventListener.handle(): run time exception");
		}
		//}
	}

}
