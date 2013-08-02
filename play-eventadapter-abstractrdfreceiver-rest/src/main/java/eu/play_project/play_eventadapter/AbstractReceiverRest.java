package eu.play_project.play_eventadapter;

import static eu.play_project.play_commons.constants.Event.WSN_MSG_DEFAULT_SYNTAX;
import static eu.play_project.play_commons.constants.Event.WSN_MSG_ELEMENT;
import static eu.play_project.play_commons.constants.Event.WSN_MSG_GRAPH_ATTRIBUTE;
import static eu.play_project.play_commons.constants.Event.WSN_MSG_NS;
import static eu.play_project.play_commons.constants.Event.WSN_MSG_SYNTAX_ATTRIBUTE;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.event_processing.events.types.Event;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.google.gson.Gson;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;

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
	
	private String playPlatformApiToken = Constants.getProperties("play-eventadapter.properties").getProperty(
			"play.platform.api.token");

	public void setApiToken(String token) {
		playPlatformApiToken = token;
	}

	private final Logger logger = LoggerFactory.getLogger(AbstractReceiverRest.class);
	private final Map<String, String> subscriptions = Collections.synchronizedMap(new HashMap<String, String>());

	/* XXX:Clients are heavy-weight objects that manage the client-side communication infrastructure.
	 *  Initialization as well as disposal of a Client instance may be a rather expensive operation.
	 *  It is therefore advised to construct only a small number of Client instances in the application
	 */
	private final Client client;


	/**
	 * Create an {@linkplain AbstractReceiverRest} using the specified PLAY DSB endpoint
	 * to make subscriptions.
	 */
	public AbstractReceiverRest(String subscribeEndpoint) {
		this.subscribeEndpoint = subscribeEndpoint;
		client = ClientBuilder.newClient();
	}

	/**
	 * Create an {@linkplain AbstractReceiverRest} using the default PLAY DSB endpoints
	 * to make subscriptions.
	 */
	public AbstractReceiverRest() {
		this(Constants.getProperties().getProperty(
				"play.platform.endpoint") + "subscriptions");
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
		
		Subscription subscription = new Subscription();
		subscription.setResource(topic + Stream.STREAM_ID_SUFFIX);
		subscription.setSubscriber(notificationsEndPoint);
		
		Gson gson = new Gson();
		// json entity of request
		Entity<String> requestEntity = Entity.json(gson.toJson(subscription));
		
		WebTarget wt = client.target(subscribeEndpoint);
		
		Response response = wt.request(MediaType.APPLICATION_JSON_TYPE)
			  .header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
			  .header("Authorization", "Bearer " + playPlatformApiToken)
			  .buildPost(requestEntity)
			  .invoke();
		
		logger.debug("Subscribe response status : "+response.getStatus());

		if(response.getStatus() != 201){
			logger.error("Subscription to '{}' at endpoint '{}' failed. HTTP Status Code: {}. {}", topic, subscribeEndpoint, response.getStatus(), response.getStatusInfo());
		}
		else{
			String responseEntity = response.readEntity(String.class);
			Subscription s = gson.fromJson(responseEntity, Subscription.class);
			subscriptions.put(s.getSubscription_id(), topic);
			logger.debug("adding subscription: id "+s.getSubscription_id());
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

		WebTarget wt = client.target(subscribeEndpoint+"/"+subscriptionId);
		
		Response response = wt.request()
			  .header("Authorization", "Bearer " + playPlatformApiToken)
			  .buildDelete()
			  .invoke();
		
		logger.debug("Unsubscribe response status : "+response.getStatus());
			//System.out.println("Unsubscribe response status: "+response.getStatus());
		if(response.getStatus() != 204){
			logger.error("Unsubscription failed. HTTP Status Code: "+response.getStatus());
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
			logger.warn(
					"Problem while unsubcribing from all subscriptions: "
							+ failCount
							+ " unsubscriptions failed at DSB endpoint '"
							+ subscribeEndpoint + "'");
		} else {
			logger.info(
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

		Gson gson = new Gson();
		
		WebTarget wt = client.target(subscribeEndpoint);
		
		Response response = wt.request(MediaType.APPLICATION_JSON_TYPE)
			  .header("Authorization", "Bearer " + playPlatformApiToken)
			  .buildGet()
			  .invoke();
		
		logger.debug("Get topics response status : "+response.getStatus());
			//System.out.println("Get topics response status: "+response.getStatus());
		if(response.getStatus() != 200){
			logger.debug("Get topics failed. HTTP Status Code: "+response.getStatus());
		}
		else{
			String responseEntity = response.readEntity(String.class);
			Subscription[] s = gson.fromJson(responseEntity, Subscription[].class);
			for(int i = 0; i < s.length; i++){
				topics.add(s[i].getResource());
			}

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
		try {
			return parseRdf(XMLHelper.createDocumentFromString(stringNotify));
		} catch (Exception e) {
			throw new NoRdfEventException("Exception while reading RDF event from XML message.", e);
		}
	}

	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param notify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(Notify notify) throws NoRdfEventException {
		for (NotificationMessageHolderType holder : notify.getNotificationMessage()) {
			// we support only one event message per notify envelope, return immediately:
			return parseRdf(holder.getMessage().getAny());
		}
		// If we reach this point past the loop, fail:
		throw new NoRdfEventException("An event was receieved without a <wsnt:Message> element.");
	}
	
	/**
	 * Retrieve RDF from the contents of an XML message.
	 * 
	 * @param xmlNotify
	 * @return
	 * @throws NoRdfEventException if there was no RDF to parse in the input
	 */
	public Model parseRdf(Node xmlNotify) throws NoRdfEventException {

		// The ModelSet to hold the RDF data:
		ModelSet rdf = EventHelpers.createEmptyModelSet();
		// The RDF syntax (serialization format):
		String syntax;
		
		//Evaluate XPath against Document itself
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(nc);
		Node playMsgElement = null;
		
		try {
			// Select the first [1] WSN_MSG_ELEMENT in document order:
			playMsgElement = (Node)xPath.evaluate("(//" + WSN_MSG_ELEMENT.getPrefix() + ":" + WSN_MSG_ELEMENT.getLocalPart() + ")[1]",
					xmlNotify, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new NoRdfEventException("An event was receieved with no or wrong content element: " + WSN_MSG_ELEMENT + ". " + e.getMessage());
		}

		String playMsgContent = (playMsgElement != null && playMsgElement.getTextContent() != null) ? playMsgElement.getTextContent() : "";
		if (playMsgContent.isEmpty()) {
			throw new NoRdfEventException("An event was receieved with no or empty content element: " + WSN_MSG_ELEMENT);
		}
		Reader r = new StringReader(playMsgContent);

		
		/*
		 * Find the RDF syntax
		 */
		Node syntaxAttribute = playMsgElement.getAttributes().getNamedItemNS(WSN_MSG_NS, WSN_MSG_SYNTAX_ATTRIBUTE);
		if (syntaxAttribute != null && !syntaxAttribute.getTextContent().isEmpty()) {
			syntax = syntaxAttribute.getTextContent();
		}
		else {
			syntax = WSN_MSG_DEFAULT_SYNTAX;
		}
		logger.debug("Parsing an incoming event with syntax '" + syntax + "'");
		
		try {
			rdf.readFrom(r, Syntax.forMimeType(syntax));
		} catch (ModelRuntimeException e) {
			throw new NoRdfEventException("An exception occured while parsing RDF of an incoming event.", e);
		} catch (IOException e) {
			throw new NoRdfEventException("An exception occured while parsing RDF of an incoming event.", e);
		}
		
		/*
		 * A hack to select the event graph in the rare case when more than one
		 * graph were returned:
		 */
		Model model = rdf.getDefaultModel();
		long max = model.size();
		Iterator<Model> it = rdf.getModels();
		// For now, select the largest model
		while (it.hasNext()) {
			Model temp = it.next();
			long tempSize = temp.size();
			if (tempSize > max) {
				max = tempSize;
				model = temp;
			}
		}
		
		if (max == 0) {
			throw new NoRdfEventException("The RDF event had no attributes, or other features (zero quads).");
		}

		// If there is no RDF context try to get it from the XML message
		if (model.getContextURI() == null) {
			Node graphAttribute = playMsgElement.getAttributes().getNamedItem(WSN_MSG_GRAPH_ATTRIBUTE);
			if (graphAttribute != null && !graphAttribute.getTextContent().isEmpty()) {
				Model temp = EventHelpers.createEmptyModel(graphAttribute.getTextContent());
				temp.addModel(model);
				model = temp;
			}

		}
		return EventHelpers.addNamespaces(model);
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
	
	@Override
	public void finalize() {
		client.close();
	}

	/**
	 * Get the defined subscribe and unsubscribe endpoint.
	 */
	public String getSubscribeEndpoint() {
		return this.subscribeEndpoint;
	}
	
	private final NamespaceContext nc = new NamespaceContext() {
		@Override
		public String getNamespaceURI(String prefix) {
			if (prefix == null)
				throw new NullPointerException("Null prefix");
			else if (WSN_MSG_ELEMENT.getPrefix().equals(prefix))
				return WSN_MSG_ELEMENT.getNamespaceURI();
			else if ("xml".equals(prefix))
				return XMLConstants.XML_NS_URI;
			return XMLConstants.NULL_NS_URI;
		}

		// This method isn't necessary for XPath processing.
		@Override
		public String getPrefix(String uri) {
			throw new UnsupportedOperationException();
		}

		// This method isn't necessary for XPath processing either.
		@SuppressWarnings("rawtypes")
		@Override
		public Iterator getPrefixes(String uri) {
			throw new UnsupportedOperationException();
		}
	};

}
