package eventcloud;

import ningyuan.pan.eventstream.EventStreamManager;
import ningyuan.pan.eventstream.ManagerRegistry;
import play.Logger;
import fr.inria.eventcloud.api.CompoundEvent;
import fr.inria.eventcloud.api.SubscriptionId;
import fr.inria.eventcloud.api.listeners.CompoundEventNotificationListener;

public class CustomEventNotificationListener extends CompoundEventNotificationListener {
	
	private static final long serialVersionUID = 1L;
	private String id;
	
	public CustomEventNotificationListener(){
		
	}
	
	public CustomEventNotificationListener(String s) {
		id = s;
		System.out.println("Orig Com Lis Thread for stream: "+id+"  "+Thread.currentThread().getId()+"  "+this.hashCode());
	}

	@Override
	public void onNotification(SubscriptionId arg0, CompoundEvent event) {
		try{
			Logger.info("Eve Lis Thread: "+Thread.currentThread().getId()+"  "+this.hashCode());
			EventStreamManager m = ManagerRegistry.getInstance().getManager(id);
			//m.notifyObserver(event);
			//manager.notifyObserver(event);
			//m.bufferEvent(event);	
		}
		catch(RuntimeException re){
			re.printStackTrace();
			Logger.info("EventListener.handle(): run time exception");
		}
	}

}
