package eu.play_project.play_eventadapter.api;

import org.event_processing.events.types.Event;
import org.ontoware.rdf2go.model.Model;

public interface RdfSender {

	public void setApiToken(String token);

	/**
	 * Send an {@linkplain Event} to the default Topic.
	 */
	public void notify(Event event);

	/**
	 * Send an {@linkplain Event} to a specific topic.
	 */
	public void notify(Event event, String topicUsed);

	/**
	 * Send a {@linkplain Model} to the default topic.
	 */
	public void notify(Model model);

	/**
	 * Send a {@linkplain Model} to a specific topic.
	 */
	public void notify(Model model, String topicUsed);

	/**
	 * Send a {@linkplain String} payload to the default topic.
	 * 
	 * The payload must be formatted in the default RDF syntax from
	 * {@linkplain eu.play_project.play_commons.constants.Event#WSN_MSG_DEFAULT_SYNTAX}
	 * .
	 */
	public void notify(String notifPayload);

	/**
	 * Send a {@linkplain String} payload to a specific topic.
	 * 
	 * The payload must be formatted in the default RDF syntax from
	 * {@linkplain eu.play_project.play_commons.constants.Event#WSN_MSG_DEFAULT_SYNTAX}
	 * .
	 */
	public void notify(String notifPayload, String topicUsed);

	/**
	 * Send a {@linkplain String} payload to a specific topic.
	 */
	public void notify(String notifPayload, String topicUsed, String notifMediatype);

	/**
	 * Get the current notify endpoint.
	 */
	public String getNotifyEndpoint();

	/**
	 * Set the default topic to be used when no topic is specified with a
	 * {@code notify} method.
	 * 
	 * @param defaultTopic
	 */
	public void setDefaultTopic(String defaultTopic);

	/**
	 * For debugging purposes: do not actually send a notification if set to {@code true}.
	 */
	public void setNoNetworking(Boolean offline);

}
