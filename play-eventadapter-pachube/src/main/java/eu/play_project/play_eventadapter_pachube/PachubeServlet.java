package eu.play_project.play_eventadapter_pachube;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;
import static eu.play_project.play_commons.constants.Namespace.EVENTS;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.event_processing.events.types.PachubeEvent;
import org.linkedopenservices.json2rdf.Transformer;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.google.gson.JsonParser;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.pachube.api.APIVersion;
import com.pachube.api.Pachube;
import com.pachube.api.Trigger;
import com.pachube.api.TriggerType;
import com.pachube.commons.impl.TriggerImpl;
import com.pachube.factory.PachubeFactory;

import eu.play_project.play_commons.constants.Source;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSender;

/**
 * A servlet to process JSON Webhook calls from Pachube. The JSON data is
 * converted into RDF and send to a WS-Notification endpoint e.g. the DSB in
 * PLAY. Optionally, the RDF data can be further processed by a SPARQL CONSTRUCT
 * query to perform transformations on the data e.g., an lifting to some
 * ontologies.
 * 
 * @author Atanas Kozarev, FZI
 * @author Roland Stühmer, FZI
 * 
 */
public class PachubeServlet extends HttpServlet implements ServletContextListener {

	private static final long serialVersionUID = 1L;
	
	private static AbstractSender eventSender = new AbstractSender(Stream.PachubeFeed.getTopicQName()) {}; 

	private static Random random = new Random();
	
	/*
	 * ============== Begin Testing ================================
	 */
	//private static Repository sesameStorage;
	/*
	 * ============== End Testing ================================
	 */

	
    public static String pachubeAdapterTriggerUrl = "http://demo.play-project.eu:8080/play-eventadapter-pachube/PachubeServlet";
    // TODO stuehmer: externalize these URIs (also for other servlets like Facebook)
    
    public static List<Integer> feedIds = Arrays.asList(18936, 9349, 8660, 8563, 10094);
    
    public static String liftingQuery = new Scanner(PachubeServlet.class.getClassLoader().getResourceAsStream("liftingQueryPachubeUpdate.ru")).useDelimiter("\\A").next();
    
	private static Pachube p;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("doGet()");

        String body = request.getParameter("body");

        System.out.println("body -> " + body);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String body = request.getParameter("body");

        Model m = createEventModel(body);

        eventSender.notify(m);

    }

	@Override
    public String getServletInfo() {
        return "Pachube event adapter for PLAY.";
    }

    public static Model createEventModel(String jsonText) {
        
		String eventId = EVENTS.getUri() + "pachube" + Math.abs(random.nextLong());
		
		// Create event with RDFReactor
		PachubeEvent event = new PachubeEvent(
				// set the RDF context part
				EventHelpers.createEmptyModel(eventId),
				// set the RDF subject
				eventId + EVENT_ID_SUFFIX,
				// automatically write the rdf:type statement
				true);

		event.setEndTime(Calendar.getInstance());
		event.setStream(new URIImpl(Stream.PachubeFeed.getUri()));
		event.setSource(new URIImpl(Source.PachubeAdapter.toString()));

		// instantiate JSON2RDF transformer to add raw data to the model:
		BlankNode rootOfRawData = event.getModel().createBlankNode();
		new Transformer(null, false).transform(new JsonParser().parse(jsonText), event.getModel(), rootOfRawData);

		// connect both graphs (both parts of the event)
		event.setRawData(rootOfRawData);

		// to use UPDATE/DELETE in queries I must alter the query syntax
		com.hp.hpl.jena.query.Syntax.defaultSyntax = com.hp.hpl.jena.query.Syntax.syntaxARQ;

		// Execute Lifting query using SPARQL Update
		//event.getModel().sparqlSelect(liftingQuery);

	    //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX OLD MODEL: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	    //event.getModel().dump();

		com.hp.hpl.jena.rdf.model.Model jenaModel = (com.hp.hpl.jena.rdf.model.Model)event.getModel().getUnderlyingModelImplementation();
		GraphStore graphStore = GraphStoreFactory.create(jenaModel);
		UpdateAction.parseExecute(liftingQuery, graphStore);

	    //System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMM NEW MODEL: MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
	    //event.getModel().dump();

		/*
		 * ============== Begin Testing ================================
		 */
		/*
		 * Create a log of events for testing purposes
		 */
//		synchronized (PachubeServlet.class) {
//			Model m = new org.openrdf.rdf2go.RepositoryModel(event.getModel().getContextURI(), sesameStorage).open();
//		    ModelUtils.copy(event.getModel(), m);
//		    m.close();
//		}
		/*
		 * ============== End Testing ================================
		 */
		
        return event.getModel();     
  	}
    
    private static void unsubscribeFromPachube() {
		try {
			URL url = new URL(pachubeAdapterTriggerUrl);
			for (Trigger t : Arrays.asList(p.getTriggers())) {
				if (url.equals(t.getUrl())) {
					p.deleteTrigger(t.getID());
				}
			}
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.WARNING, "Unsubscribing from old Pachube feeds failed. ", e);
		}
    }
    
    private static void subscribeToPachube() {
		/*
		 *  Loop through a list of feeds to subscribe to Pachube:
		 */
		for (int feedId : feedIds) {
			try {
				
				Trigger t = new TriggerImpl();
				t.setEnv_id(feedId);
				t.setStream_id(0);

				t.setType(TriggerType.CHANGE);

				t.setUrl(new URL(pachubeAdapterTriggerUrl));

				Logger.getAnonymousLogger().info("Subscribing to Pachube at feed Id " + feedId);
				p.createTrigger(t);
			} catch (Exception e) {
				Logger.getAnonymousLogger().log(Level.WARNING, "Subscribing to Pachube feed " + feedId + " failed. ", e);
			}
		}
    }
    
    public static synchronized void initSesame() {
		/*
		 * ============== Begin Testing ================================
		 */
//		try {
//			if (sesameStorage == null) {
//				File path = new File(System.getProperty("java.io.tmpdir"));
//
//				Logger.getAnonymousLogger().log(Level.INFO,
//						"Sesame path: " + path);
//
//				File dataDir = new File(path
//						+ "/play-eventadapter-pachube/sesame/");
//				sesameStorage = new SailRepository(new MemoryStore(dataDir));
//				sesameStorage.initialize();
//			}
//		} catch (RepositoryException e) {
//			Logger.getAnonymousLogger().log(Level.WARNING,
//					"Problem while initializing Sesame storage.", e);
//		}		
		/*
		 * ============== End Testing ================================
		 */

    }
    
    public static synchronized void destroySesame() {
		/*
		 * ============== Begin Testing ================================
		 */
//		if (sesameStorage != null) {
//			try {
//				sesameStorage.shutDown();
//			} catch (RepositoryException e) {
//				Logger.getAnonymousLogger().log(Level.WARNING,
//						"Problem while shutting down Sesame storage.", e);
//			}
//		}
		/*
		 * ============== End Testing ================================
		 */
 	
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		Logger.getAnonymousLogger().log(Level.INFO,
				"Context initialized.");

		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader().getResourceAsStream("pachube.properties"));

		
			if (p == null) {
				p = (new PachubeFactory()).createPachube(
						properties.getProperty("pachube.v2.apiKey"), APIVersion.V2);
			}
			initSesame();
			/* 
			 * We are doing this here and not in init() because we need subscriptions
			 * before the servlet is invoked (and thus init would be called):
			 */
			subscribeToPachube();
		}
		catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.WARNING,
					"Exception while initializing servlet: ", e);
		}
	}

	@Override
	public void init() throws ServletException {
		Logger.getAnonymousLogger().log(Level.INFO,
				"Servlet init.");
		super.init();
	}
	
	@Override
	public void destroy() {
		unsubscribeFromPachube();
		destroySesame();
		super.destroy();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
