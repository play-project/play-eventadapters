package eu.play_project.play_eventadapter;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.ontoware.rdf2go.model.Model;
import org.w3c.dom.Element;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.eventtypes.EventHelpers;

public class AbstractSenderRest {

	/** Default REST endpoint for notifications */
	private String notifyEndpoint = Constants.getProperties().getProperty(
			"play.platform.endpoint") + "publish";
	/** Credentials for publishing events to PLAY Platform */
	private final String PLAY_PLATFORM_APITOKEN = Constants.getProperties("play-eventadapter.properties").getProperty(
			"play.platform.api.token");

	private final Logger logger = Logger.getAnonymousLogger();
	private QName defaultTopic;
	private Boolean online = true;

	public AbstractSenderRest(QName defaultTopic) {
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
	public void notify(Event event, QName topicUsed) {
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
	public void notify(Model model, QName topicUsed) {
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
	public void notify(String notifPayload, QName topicUsed) {
		
		// TODO ningyuan
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
	public void setDefaultTopic(QName defaultTopic) {
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
