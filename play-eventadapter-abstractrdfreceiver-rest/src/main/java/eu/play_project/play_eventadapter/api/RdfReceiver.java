package eu.play_project.play_eventadapter.api;

import java.util.List;

import org.event_processing.events.types.Event;
import org.ontoware.rdf2go.model.Model;
import org.w3c.dom.Node;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;

import eu.play_project.play_eventadapter.AbstractReceiverRest;
import eu.play_project.play_eventadapter.NoRdfEventException;

public interface RdfReceiver {

	public void setApiToken(String token);

	/**
	 * Subscribe to a topic at the endpoint in
	 * {@link AbstractReceiverRest#AbstractReceiverRest(String)}. The callback
	 * will be used by the DSB to send the subscriptions. It is your
	 * responsibility to prevent/avoid duplicate subscriptions if needed.
	 * 
	 * @param topic
	 * @param notificationsEndPoint
	 *            callback SOAP URI to receive notifications
	 * @return subscription ID as URI resource
	 */
	public String subscribe(String topic, String notificationsEndPoint);

	/**
	 * Unsubscribe from a specific subscription.
	 * 
	 * @param subscriptionId
	 */
	public void unsubscribe(String subscriptionId);

	/**
	 * Unsubscribe from all previous subscriptions. Don't fail if something goes wrong.
	 */
	public void unsubscribeAll();

	/**
	 * Retreive the current list of topics from the endpoint in
	 * {@link AbstractReceiverRest#subscribeEndpoint}.
	 * 
	 * @return the current list of topics
	 */
	public List<String> getTopics();

	/**
	 * Retrieve RDF from the contents of the "message" attribute in a REST call.
	 * 
	 * @param rdf
	 *            is expected to contain RDF using the syntax declared in
	 *            {@linkplain eu.play_project.play_commons.constants.Event#WSN_MSG_DEFAULT_SYNTAX}
	 *            .
	 */
	public Model parseRdfRest(String rdf) throws NoRdfEventException;

	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param stringNotify a String containing a Notify SOAP message
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(String stringNotify) throws NoRdfEventException;

	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param notify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(Notify notify) throws NoRdfEventException;

	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(Node xmlNotify) throws NoRdfEventException;

	/**
	 * Retrieve an Event from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @param type the event class which is expected as a return type
	 * @return an event of the given type, if one was found. {@code null} otherwise
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public <EventType extends Event> EventType getEvent(String stringNotify, Class<EventType> type)
			throws NoRdfEventException;

	/**
	 * Retrieve an Event from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @param type the event class which is expected as a return type
	 * @return an event of the given type, if one was found. {@code null} otherwise
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public <EventType extends Event> EventType getEvent(Node xmlNotify, Class<EventType> type)
			throws NoRdfEventException;

	/**
	 * Get the defined subscribe and unsubscribe endpoint.
	 */
	public String getSubscribeEndpoint();

}
