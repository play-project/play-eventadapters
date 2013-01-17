package ningyuan.pan.eventstream;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import play.Logger;
import play.libs.F;
import play.mvc.Results.StringChunks;

/**
 * @author Ningyuan Pan
 *
 */
public class RDFHTTPStream extends StringChunks implements HTTPStreamObserver<EventRDFSyntaxTranslator>{
	
	//
	private final ReentrantLock lock = new ReentrantLock();
	//
	private final Condition newMsg = lock.newCondition();
	//
	private String msg = new String();
	//
	private volatile boolean connected = true;
	
	private final String id;
	private final String RDFSyntax;
	private final EventStreamManager manager;

	
	public RDFHTTPStream (String s, String syn, String c, EventStreamManager sm){
		super(c);
		id = s;
		RDFSyntax = syn;
		manager = sm;
		init();
	}
		
	@Override
	public String getID(){
		return id;
	}
	
	@Override
	public void onReady(play.mvc.Results.Chunks.Out<String> out) {
		// run in the same thread
		
		out.onDisconnected(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				connected = false;
			}
		});
		
		out.write("Waiting for events from event cloud...\n" +
				"*** Please stop the page when you don't want to listen on this stream ***\n\n\n");
		while(connected){
			System.out.println("<"+id+">"+" RDFHTTPStream await: ");
			try{
				this.lock.lock();
				newMsg.await();
				out.write(msg+"\n");
			}catch (InterruptedException ie) {
				ie.printStackTrace();
				break;
			}
			finally{
				this.lock.unlock();
			}
		}
		out.close();
		manager.removeObserver(this);
		
	}
	
	@Override
	public void update(EventRDFSyntaxTranslator translator){
		try{
			this.lock.lock();
			msg = translator.RDFinSyntax(RDFSyntax);
			newMsg.signal();
		}
		finally{
			this.lock.unlock();
				System.out.println("<"+id+">"+" RDFHTTPStream update: ");
		}
	}
	
	private void init(){
		manager.addObserver(this);
	}
}
