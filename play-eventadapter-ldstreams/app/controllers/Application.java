package controllers;

import java.util.UUID;

import ningyuan.pan.eventstream.EventStreamManager;
import ningyuan.pan.eventstream.HTTPBufferStream;
import ningyuan.pan.eventstream.ManagerRegistry;
import ningyuan.pan.util.AcceptHeaderParser;
import ningyuan.pan.util.RDFSyntaxHeaderRegister;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;
import fr.inria.eventcloud.adapters.rdf2go.SubscribeRdf2goAdapter;
import fr.inria.eventcloud.api.EventCloudId;
import fr.inria.eventcloud.api.SubscribeApi;
import fr.inria.eventcloud.exceptions.EventCloudIdNotManaged;
import fr.inria.eventcloud.factories.ProxyFactory;

/**
 * @author Ningyuan Pan
 *
 */
public class Application extends Controller {
	  
	  // data structure to keep all the event managers
	  private static ManagerRegistry managers = ManagerRegistry.getInstance();
	  
	  public static Result index() {
		  return ok(index.render("Your new application is ready."));
	  }
	  
	  /**
	   * Get RDF stream with id from event cloud.
	   * @param id
	   * @return
	   */
	  public static Result getStream(String id){
	      // test whether EC registry is returned
		  if(MyGlobalSetting.EC_Connected){
			  	 Logger.info("User-Agent: "+request().getHeader("User-Agent"));
			  String RDFSyntax = getRDFSyntaxType();
	          	 Logger.info("Client preferrs: "+RDFSyntax);
	          
	          // set response head information
	          if(RDFSyntax == null){
	        	  return status(Http.Status.NOT_ACCEPTABLE);
	          }
	          else if(RDFSyntax.equalsIgnoreCase(RDFSyntaxHeaderRegister.PLAIN)){
	        	  response().setHeader("content-type", "text/plain");
	        	  RDFSyntax = RDFSyntaxHeaderRegister.DEFAULT_SYNTAX;
	          }
	          else{
	        	  response().setHeader("content-type", RDFSyntax);
	          }
	          
	          	 Logger.info("RDF Sytax: "+RDFSyntax);
	          // for each event stream there should be only one manager
	          EventStreamManager manager = getManager(id);
	          if(manager == null)
	        	  return notFound("The stream is not available.");
	          else{
	        	  response().setHeader("connection", "keep-alive");
	        	  HTTPBufferStream chunkStream = new HTTPBufferStream(UUID.randomUUID().toString(), RDFSyntax, "UTF-8", manager);
	        	  //RDFHTTPStream chunkStream = new RDFHTTPStream(UUID.randomUUID().toString(), RDFSyntax, "UTF-8", manager);
	        	  return ok(chunkStream);
	          }
		  }
		  else{
			  return notFound("The event cloud can not be connected.");
		  }
	  }
	  
	  /*
	   * Read MIME info from accept header in Http request. Return which syntax of
	   * RDF is preferred by client.
	   */
	  private static String getRDFSyntaxType(){
		  RDFSyntaxHeaderRegister register = new RDFSyntaxHeaderRegister();
    	  AcceptHeaderParser parser = new AcceptHeaderParser(register);
    	  parser.parse(request().getHeader(ACCEPT));
    	  return register.getPreferredRDFSyntax();
	  }
	  
	  /*
	   * Get the corresponding EventStreamManager for stream with id.
	   */
	  private static EventStreamManager getManager(String id){
		  EventStreamManager ret = null;
    	  EventCloudId ecId = new EventCloudId(MyGlobalSetting.STREAMS_URI+id);
    	  	 Logger.info("Get Event Manager to Stream: "+ecId.toString());
    	  if(MyGlobalSetting.registry.contains(ecId)){
    		  synchronized(managers){
    			  ret = managers.getManager(id);
    		  
    			  if(ret == null){
    				  SubscribeApi subscribeProxy;
    				  try {
    					  Logger.info("Getting proxy...");
    					  subscribeProxy = ProxyFactory.newSubscribeProxy(MyGlobalSetting.registryName, ecId);
    	    		  } catch (EventCloudIdNotManaged e) {
    					  Logger.error("Subscribe proxy for event stream "+id+" could not created.");
    	  		   	 	  return ret;
    				  }
    				  
    				  //create DB_TABLE_SUBID for this stream
    				  if(MyGlobalSetting.db.getHashSet(id) == null){
    					  MyGlobalSetting.subbedStreams.add(id);
    					  MyGlobalSetting.db.createHashSet(id);
    				  }
    				  
    				  String query = new String(MyGlobalSetting.QUERY_STRING);
					  SubscribeRdf2goAdapter subscribeRdfProxy = new SubscribeRdf2goAdapter(subscribeProxy);
					  ret = new EventStreamManager(subscribeRdfProxy, query, id, MyGlobalSetting.CACHE_DB_LOCATION);
    				  //ret = new EventStreamManager(subscribeProxy, query, id, MyGlobalSetting.CACHE_DB_LOCATION);
    				  
    				  managers.setManager(id, ret);
    			  }
    		  }
    		  return ret;
    	  }
    	  else{
    		  return ret;
    	  }
	 }
}