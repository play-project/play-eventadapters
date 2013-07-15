package eu.play_project.play_eventadapter;

import static eu.play_project.play_commons.constants.Event.WSN_MSG_DEFAULT_SYNTAX;

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
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;

public class AbstractSenderRest {
	
	/** Default REST endpoint for notifications */
	private String notifyEndpoint = Constants.getProperties().getProperty(
			"play.platform.endpoint") + "publish";
	
	/** Credentials for publishing events to PLAY Platform */
	private final String PLAY_PLATFORM_APITOKEN = Constants.getProperties("play-eventadapter.properties").getProperty(
			"play.platform.api.token");

	private final Logger logger = Logger.getAnonymousLogger();
	private String defaultTopic;
	private Boolean online = true;
	private final Client client;
	private final WebTarget webTarget;
	
	public AbstractSenderRest(String defaultTopic) {
		this.defaultTopic = defaultTopic;
		client = ClientBuilder.newClient();
		webTarget = client.target(notifyEndpoint);
	}

	public AbstractSenderRest(QName defaultTopic) {
		this(defaultTopic.getNamespaceURI() + defaultTopic.getLocalPart());
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
		notify(model.serialize(Syntax.forMimeType(WSN_MSG_DEFAULT_SYNTAX)), topicUsed);
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
		
		MultivaluedMap<String, String> data = new MultivaluedHashMap<String, String>();
		data.add("resource", topicUsed + Stream.STREAM_ID_SUFFIX);
		data.add("message", notifPayload);
		// form entity of request
		Entity<Form> entity = Entity.form(data);
		
		if (online) {
			Response response = webTarget.request()
				  .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				  .header("Authorization", "Bearer " + PLAY_PLATFORM_APITOKEN)
				  .buildPost(entity)
				  .invoke();
			
			if(response.getStatus() != 200){
				logger.log(Level.SEVERE, "No event was notified because of response status "+response.getStatus() + " " + response.getStatusInfo());
			}
			logger.fine("Response status: "+response.getStatus());
			response.close();
		}
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
	
	@Override
	public void finalize() {
		client.close();
	}
}
