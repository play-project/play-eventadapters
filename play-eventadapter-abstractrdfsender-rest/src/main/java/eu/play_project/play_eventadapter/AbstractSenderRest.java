package eu.play_project.play_eventadapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.ontoware.rdf2go.model.Model;
import org.w3c.dom.Element;



import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;

public class AbstractSenderRest {
	
	/* XXX:Clients are heavy-weight objects that manage the client-side communication infrastructure.
	 *  Initialization as well as disposal of a Client instance may be a rather expensive operation. 
	 *  It is therefore advised to construct only a small number of Client instances in the application
	 */
	private ClientBuilder cBuilder = new JerseyClientBuilder();
	
	/** Default REST endpoint for notifications */
	private String notifyEndpoint = Constants.getProperties().getProperty(
			"play.platform.endpoint") + "publish";
	/** Credentials for publishing events to PLAY Platform */
	private final String PLAY_PLATFORM_APITOKEN = Constants.getProperties("play-eventadapter.properties").getProperty(
			"play.platform.api.token");

	private final Logger logger = Logger.getAnonymousLogger();
	private String defaultTopic;
	private Boolean online = true;
	
	public AbstractSenderRest(String defaultTopic) {
		this.defaultTopic = defaultTopic;
	}

	/**
	 * Send an {@linkplain Event} to the default Topic.
	 */
	public void notify(Event event) {
		notify(event, this.defaultTopic);
	}

	/**
	 * Send an {@linkplain Event} to a specific topic.
	 */
	public void notify(Event event, String topicUsed) {
		notify(event.getModel(), topicUsed);
	}

	/**
	 * Send a {@linkplain Model} to the default topic.
	 */
	public void notify(Model model) {
		notify(model, this.defaultTopic);
	}

	/**
	 * Send a {@linkplain Model} to a specific topic.
	 */
	public void notify(Model model, String topicUsed) {
		Element element = EventHelpers.serializeAsDom(model);

		// TODO stuehmer

	}

	/**
	 * Send a {@linkplain String} payload to the default topic.
	 */
	public void notify(String notifPayload) {
		notify(notifPayload, this.defaultTopic);
	}

	/**
	 * Send a {@linkplain String} payload to a specific topic.
	 */
	public void notify(String notifPayload, String topicUsed) {
		
		// XXX performance problem with new client
		Client client = cBuilder.newClient();
		
		MultivaluedMap<String, String> data = new MultivaluedHashMap<String, String>();
		data.add("resource", topicUsed + Stream.STREAM_ID_SUFFIX);
		data.add("message", notifPayload);
		// form entity of request
		Entity<Form> entity = Entity.form(data);
		
		WebTarget wt = client.target(notifyEndpoint);
		
		Response response = wt.request()
			  .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_TYPE)
			  .header("Authorization", "Bearer " + PLAY_PLATFORM_APITOKEN)
			  .buildPost(entity)
			  .invoke();
		
		client.close();
		
		if(response.getStatus() != 200){
			logger.log(Level.SEVERE, "No event was notified because of response status "+response.getStatus());
		}
		logger.fine("Response status : "+response.getStatus());
		response.close();
	}

	/**
	 * Overwrite the default notify endpoint.
	 * 
	 * @param notifyEndpoint
	 */
	public void setNotifyEndpoint(String dsbNotify) {
		this.notifyEndpoint = dsbNotify;
	}

	/**
	 * Get the current notify endpoint.
	 */
	public String getNotifyEndpoint() {
		return this.notifyEndpoint;
	}
	
	/**
	 * Set the default topic to be used when no topic is specified with a
	 * {@code notify} method.
	 * 
	 * @param defaultTopic
	 */
	public void setDefaultTopic(String defaultTopic) {
		if (defaultTopic == null) {
			throw new NullPointerException("defaultTopic may not be null");
		}
		this.defaultTopic = defaultTopic;
	}

	/**
	 * For debugging purposes: do not actually send a notification.
	 */
	public void setNoNetworking(Boolean offline) {
		this.online = !offline;
	}
}
