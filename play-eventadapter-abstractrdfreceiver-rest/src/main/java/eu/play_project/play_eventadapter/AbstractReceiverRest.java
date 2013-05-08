package eu.play_project.play_eventadapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.event_processing.events.types.Notify;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.ontoware.rdf2go.model.Model;
import org.w3c.dom.Node;

import com.google.gson.Gson;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;

/**
 * A an abstract event consumer which can subscribe to PLAY RDF events and deal
 * with the necessary un-marshalling and parsing.
 * 
 * @author stuehmer
 * 
 */
public abstract class AbstractReceiverRest {

	/** Default REST endpoint for notifications */
	private final String subscribeEndpoint;
	
	private final String PLAY_PLATFORM_APITOKEN = Constants.getProperties("play-eventadapter.properties").getProperty(
			"play.platform.api.token");

	private final Logger logger = Logger.getAnonymousLogger();
	private final Map<String, String> subscriptions = Collections.synchronizedMap(new HashMap<String, String>());
	
	/* XXX:Clients are heavy-weight objects that manage the client-side communication infrastructure.
	 *  Initialization as well as disposal of a Client instance may be a rather expensive operation. 
	 *  It is therefore advised to construct only a small number of Client instances in the application
	 */
	private ClientBuilder cBuilder = new JerseyClientBuilder();
	
	/**
	 * Create an {@linkplain AbstractReceiverRest} using the specified PLAY DSB endpoint
	 * to make subscriptions.
	 */
	public AbstractReceiverRest(String subscribeEndpoint) {
		this.subscribeEndpoint = subscribeEndpoint;
	}

	/**
	 * Create an {@linkplain AbstractReceiverRest} using the default PLAY DSB endpoints
	 * to make subscriptions.
	 */
	public AbstractReceiverRest() {
		this.subscribeEndpoint = Constants.getProperties().getProperty(
				"play.platform.endpoint") + "subscriptions";
	}

	/**
	 * Subscribe to a topic at the endpoint in
	 * {@link AbstractReceiverRest#subscribeEndpoint}. The callback will be used by the
	 * DSB to send the subscriptions. It is your responsibility to prevent/avoid
	 * duplicate subscriptions if needed.
	 * 
	 * @param topic
	 * @param notificationsEndPoint
	 *            callback SOAP URI to receive notifications
	 * @return subscription ID as URI resource
	 */
	public String subscribe(String topic, String notificationsEndPoint)  {
		
		String subscriptionResourceUrl = "";
		// XXX performance problem with new client
		Client client = cBuilder.newClient();
		
		Subscription subscription = new Subscription();
		subscription.setResource(topic + Stream.STREAM_ID_SUFFIX);
		subscription.setSubscriber(notificationsEndPoint);
		
		Gson gson = new Gson();
		// json entity of request
		Entity<String> requestEntity = Entity.json(gson.toJson(subscription));
		
		WebTarget wt = client.target(subscribeEndpoint);
		
		Response response = wt.request(MediaType.APPLICATION_JSON_TYPE)
			  .header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
			  .header("Authorization", "Bearer " + PLAY_PLATFORM_APITOKEN)
			  .buildPost(requestEntity)
			  .invoke();

		client.close();
		
		logger.fine("Subscribe response status : "+response.getStatus());
			//System.out.println("Subscribe response status: "+response.getStatus());
		if(response.getStatus() != 201){
			logger.log(Level.SEVERE, "Subscription failed. "+response.getStatus());
		}
		else{
			String responseEntity = response.readEntity(String.class);
			Subscription s = gson.fromJson(responseEntity, Subscription.class);
			subscriptions.put(s.getSubscription_id(), topic);
				//System.out.println("add sub: id"+s.getSubscription_id()+"\n");
			subscriptionResourceUrl = s.getSubscription_id();
				
		}
		response.close();
		return subscriptionResourceUrl;
	}

	/**
	 * Unsubscribe from a specific subscription.
	 * 
	 * @param subscriptionId
	 */
	public void unsubscribe(String subscriptionId) {
		
		// XXX performance problem with new client
		Client client = cBuilder.newClient();
		
		WebTarget wt = client.target(subscribeEndpoint+"/"+subscriptionId);
		
		Response response = wt.request()
			  .header("Authorization", "Bearer " + PLAY_PLATFORM_APITOKEN)
			  .buildDelete()
			  .invoke();

		client.close();
		
		logger.fine("Unsubscribe response status : "+response.getStatus());
			//System.out.println("Unsubscribe response status: "+response.getStatus());
		if(response.getStatus() != 204){
			logger.log(Level.SEVERE, "Unsubscription failed. "+response.getStatus());
		}
		else{
			subscriptions.remove(subscriptionId);
				//System.out.println("remove sub: id"+subscriptionId+"\n");
		}
		response.close();
	}

	/**
	 * Unsubscribe from all previous subscriptions. Don't fail if something goes wrong.
	 */
	public void unsubscribeAll() {
		int failCount = 0;
		// Make a copy of the collection because it will be modified in #unsubscribe()
		Set<String> removal = new HashSet<String>(subscriptions.keySet());
		for (String subscriptionID : removal) {
			unsubscribe(subscriptionID);
		}
		if (failCount > 0) {
			logger.log(Level.WARNING,
					"Problem while unsubcribing from all subscriptions: "
							+ failCount
							+ " unsubscriptions failed at DSB endpoint '"
							+ subscribeEndpoint + "'");
		} else {
			logger.log(Level.INFO,
					"Successfully unsubcribed from all subscriptions at DSB endpoint '"
							+ subscribeEndpoint + "'");
		}
	}

	/**
	 * Retreive the current list of topics from the endpoint in
	 * {@link AbstractReceiverRest#subscribeEndpoint}.
	 * 
	 * @return the current list of topics
	 */
	public List<String> getTopics() {

		List<String> topics = new ArrayList<String>();
		
		// XXX performance problem with new client
		Client client = cBuilder.newClient();
		
		Gson gson = new Gson();
		
		WebTarget wt = client.target(subscribeEndpoint);
		
		Response response = wt.request(MediaType.APPLICATION_JSON_TYPE)
			  .header("Authorization", "Bearer " + PLAY_PLATFORM_APITOKEN)
			  .buildGet()
			  .invoke();

		client.close();
		
		logger.fine("Get topics response status : "+response.getStatus());
			//System.out.println("Get topics response status: "+response.getStatus());
		if(response.getStatus() != 200){
			logger.log(Level.SEVERE, "Get topics failed. "+response.getStatus());
		}
		else{
			String responseEntity = response.readEntity(String.class);
			Subscription[] s = gson.fromJson(responseEntity, Subscription[].class);
			for(int i = 0; i < s.length; i++){
				topics.add(s[i].getResource());
					//System.out.println("topic: "+s[i].getResource());
					//System.out.println("id: "+s[i].getSubscription_id());
			}
				//System.out.println();
		}
		response.close();
		
		return topics;
		
	}

	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param stringNotify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(String stringNotify) throws NoRdfEventException {
		
		throw new UnsupportedOperationException("not implemented yet");
		// TODO stuehmer
	}
	
	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param notify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(Notify notify) throws NoRdfEventException {
		throw new UnsupportedOperationException("not implemented yet");
		// TODO stuehmer
	}
		
	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(Node xmlNotify) throws NoRdfEventException {

		throw new UnsupportedOperationException("not implemented yet");
		// TODO stuehmer

	}
	
	/**
	 * Retrieve an Event from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @param type the event class which is expected as a return type
	 * @return an event of the given type, if one was found. {@code null} otherwise
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public <EventType extends Event> EventType getEvent(String stringNotify, Class<EventType> type) throws NoRdfEventException {
		throw new UnsupportedOperationException("not implemented yet");
		// TODO stuehmer

	}

	/**
	 * Retrieve an Event from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @param type the event class which is expected as a return type
	 * @return an event of the given type, if one was found. {@code null} otherwise
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public <EventType extends Event> EventType getEvent(Node xmlNotify, Class<EventType> type) throws NoRdfEventException {
		throw new UnsupportedOperationException("not implemented yet");
		// TODO stuehmer

	}

	/**
	 * Get the defined subscribe and unsubscribe endpoint.
	 */
	public String getSubscribeEndpoint() {
		return this.subscribeEndpoint;
	}
	
}
