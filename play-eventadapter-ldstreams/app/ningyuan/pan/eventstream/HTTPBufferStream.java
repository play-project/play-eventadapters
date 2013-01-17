/**
 * 
 */
package ningyuan.pan.eventstream;


import ningyuan.pan.util.Buffer;
import play.Logger;
import play.libs.F;
import play.mvc.Results.StringChunks;

/**
 * @author Ningyuan Pan
 *
 */
public class HTTPBufferStream extends StringChunks implements HTTPStreamObserver<EventRDFSyntaxTranslator>, Runnable{
	
	private Buffer<EventRDFSyntaxTranslator> buffer = new Buffer<EventRDFSyntaxTranslator>(512);
	// the connection of chunk stream
	private volatile boolean connected = true;
	
	private final String id;
	private final String RDFSyntax;
	private final EventStreamManager manager;
	
	private play.mvc.Results.Chunks.Out<String> outStream;
	private Thread writingt;
	
	public HTTPBufferStream (String s, String syn, String c, EventStreamManager sm){
		super(c);
		id = s;
		RDFSyntax = syn;
		manager = sm;
		manager.addObserver(this);
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
				if(writingt != null)
					writingt.interrupt();
			}
		});
		
		outStream = out;
		writingt = new Thread(this);
		writingt.start();

	}
	
	@Override
	public void update(EventRDFSyntaxTranslator translator){
		try {
			buffer.add(translator);
				//Logger.info("Buffer "+buffer.getSize());
		} catch (InterruptedException e) {
			e.printStackTrace();
			// XXX
			Logger.info("!!!!!!!!");
		}
		
		System.out.println("<"+id+">"+" HTTPBufferStream update: ");
	}

	@Override
	public void run() {
		try{
			outStream.write("------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                Waiting for events from event cloud...                ----\n"+
							"----  Please stop the page when you don't want to listen on this stream   ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"----                                                                      ----\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n" +
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n"+
							"------------------------------------------------------------------------------\n");
			
			
			while(connected){
				System.out.println("<"+id+">"+" HTTPBufferStream await: "+Thread.currentThread().getId());
				String msg = buffer.get().getEventInSyntax(RDFSyntax);
				outStream.write(msg+"\n");
			}
		}
		catch (InterruptedException e) {
			Logger.info("HTTPStream Writing Thread is interrupted.");
		}
		finally{
			outStream.write("\n\n\nSteam stoped\n\n\n");
			outStream.close();
			manager.removeObserver(this);
			Logger.info("HTTPStream Writing Thread is stoped.");
		}
	}
}
