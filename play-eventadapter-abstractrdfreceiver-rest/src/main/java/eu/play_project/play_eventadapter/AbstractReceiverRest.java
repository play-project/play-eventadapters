package eu.play_project.play_eventadapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.event_processing.events.types.Notify;
import org.ontoware.rdf2go.model.Model;
import org.w3c.dom.Node;

import eu.play_project.play_commons.constants.Constants;

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

	private final Logger logger = Logger.getAnonymousLogger();
	private final Map<String, QName> subscriptions = Collections.synchronizedMap(new HashMap<String, QName>());

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
	public String subscribe(QName topic, String notificationsEndPoint)  {

		String subscriptionResourceUrl = "";
		
		// TODO ningyuan
		
		return subscriptionResourceUrl;
	}

	/**
	 * Unsubscribe from a specific subscription.
	 * 
	 * @param subscriptionId
	 */
	public void unsubscribe(String subscriptionId) {
		
		// TODO ningyuan
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
	public List<QName> getTopics() {

		List<QName> topics;

		throw new UnsupportedOperationException("not implemented yet");
		// TODO ningyuan, later
		
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
