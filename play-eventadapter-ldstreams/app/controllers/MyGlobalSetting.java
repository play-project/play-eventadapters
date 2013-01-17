package controllers;

import java.util.Set;

import org.apache.jdbm.DB;

import eu.play_project.play_commons.constants.Namespace;
import eventcloud.EventCloud;
import fr.inria.eventcloud.EventCloudsRegistry;

import ningyuan.pan.eventstream.ManagerRegistry;
import ningyuan.pan.util.Persistence;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class MyGlobalSetting extends GlobalSettings {
	  
	  /*
	   * describe whether a cache for subscripted streams is enable
	   */
	  public static boolean Cache_Enable = false;
	  static DB db;
	  static Set<String> subbedStreams;
	  public static final String CACHE_DB_LOCATION = "./db";
	  public static final String DB_TABLE_STREAMS = "subbedstreams";
	  
	  static boolean EC_Connected = false;
	  static String registryName;
	  static EventCloudsRegistry registry;
	  
	  static final String STREAMS_URI = Namespace.STREAMS.getUri();
	  //static final String QUERY_STRING = "SELECT ?s ?p ?o WHERE { GRAPH ?g{ ?s ?p ?o }}";
	  //static final String QUERY_STRING = "SELECT ?g ?s ?p ?o WHERE { GRAPH ?g { ?s ?p ?o } }";
	  static final String QUERY_STRING = "SELECT ?id1 ?s ?p ?o WHERE { GRAPH ?id1 { ?s ?p ?o }}";
	  
	  @Override
	  public void onStart(Application app) {
		  init();
		  Logger.info("Application is started");
	  }  
	  
	  @Override
	  public void onStop(Application app) {
		  ManagerRegistry.getInstance().close();
		  Persistence.close();
		  Logger.info("Application is shutdown");
	  }  
	  
	  private void init(){
		  loadCachedata();
		  connectEventCloud();
	  }
	  
	  private void connectEventCloud(){
		  Logger.info("Getting event cloud registry...");
		  
		  registryName = EventCloud.getNameofRegistry();
		  registry = EventCloud.getInstance().getRegistry();
		  if(registry != null)
			  EC_Connected = true;
		  
	  }
	  
	  /*
	   * Load data of unsubscribed streams from last shutdown.
	   */
	  private void loadCachedata(){
		  db = Persistence.connect(CACHE_DB_LOCATION);
		  subbedStreams = db.getHashSet(DB_TABLE_STREAMS);
		  if(subbedStreams == null){
			  subbedStreams = db.createHashSet(DB_TABLE_STREAMS);
			      Logger.info("create hash DB_TABLE_STREAMS");
		  }
		  else{
			  for(String s : subbedStreams){
				  Logger.info("subbed stream: "+s.toString());
			  }
		  }
		  Cache_Enable = true;
	  }
}
