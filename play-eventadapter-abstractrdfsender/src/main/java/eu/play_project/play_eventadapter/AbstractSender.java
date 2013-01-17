package eu.play_project.play_eventadapter;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.event_processing.events.types.Event;
import org.ontoware.rdf2go.model.Model;
import org.petalslink.dsb.notification.client.http.HTTPNotificationConsumerClient;
import org.petalslink.dsb.notification.commons.NotificationHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.WstopConstants;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.eventtypes.EventHelpers;

public class AbstractSender {

	/*
	 * A static initializer needed for the WS-N utilities.
	 */
	static {
		Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(), new WsrfrModelFactoryImpl(),
				new WsrfrlModelFactoryImpl(), new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
				new WsnbModelFactoryImpl());
	}

	/** Default SOAP endpoint for notifications */
	private String dsbNotify = Constants.getProperties().getProperty(
			"dsb.notify.endpoint");
	private Logger logger = Logger.getAnonymousLogger();
	private QName defaultTopic;
	private String producerAddress = "http://localhost:9998/foo/"
			+ AbstractSender.class.getSimpleName();
	private String endpointAddress = "http://localhost:9998/foo/Endpoint";
	private Boolean online = true;

	public AbstractSender(QName defaultTopic) {
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
	 * Send a {@linkplain Model} to the default Topic.
	 */
	public void notify(Model model) {
		notify(model, this.defaultTopic);
	}

	/**
	 * Send a {@linkplain Model} to a specific topic.
	 */
	public void notify(Model model, QName topicUsed) {
		Element element = EventHelpers.serializeAsDom(model);

		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			doc.appendChild(doc.adoptNode(element.cloneNode(true)));
			notify(doc, topicUsed);
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "No event was notified because of: ", e);
		}

	}

	/**
	 * Send a {@linkplain String} payload to the default Topic.
	 */
	public void notify(String notifPayload) {
		notify(notifPayload, this.defaultTopic);
	}

	/**
	 * Send a {@linkplain String} payload to a specific topic.
	 */
	public void notify(String notifPayload, QName topicUsed) {
		try {
			Document notifPayloadDoc = XMLHelper
					.createDocumentFromString(notifPayload);
			notify(notifPayloadDoc, topicUsed);
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "No event was notified because of: ", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "No event was notified because of: ", e);
		}
	}

	/**
	 * Send a {@linkplain Document} payload to the default Topic.
	 */
	public void notify(Document notifPayload) {
		notify(notifPayload, this.defaultTopic);
	}

	/**
	 * Send a {@linkplain Document} payload to a specific topic.
	 */
	public void notify(Document notifPayload, QName topicUsed) {
		String uuid = UUID.randomUUID().toString();

		String dialect = WstopConstants.CONCRETE_TOPIC_EXPRESSION_DIALECT_URI
				.toString();

		Notify notify;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			notify = NotificationHelper.createNotification(producerAddress,
					endpointAddress, uuid, topicUsed, dialect, notifPayload);
			Document dom = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(
					notify);

			INotificationConsumer consumerClient = new HTTPNotificationConsumerClient(
					dsbNotify);
			if (online) {
				consumerClient.notify(notify);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "No event was notified because of: ", e);
		}
	}

	/**
	 * Overwrite the default notify endpoint.
	 * 
	 * @param dsbNotify
	 */
	public void setDsbNotify(String dsbNotify) {
		this.dsbNotify = dsbNotify;
	}

	/**
	 * Get the current notify endpoint.
	 */
	public String getDsbNotify() {
		return this.dsbNotify;
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

	public String getProducerAddress() {
		return producerAddress;
	}

	/**
	 * Optionally specify the producer address of this Sender.
	 */
	public void setProducerAddress(String producerAddress) {
		this.producerAddress = producerAddress;
	}

	public String getEndpointAddress() {
		return endpointAddress;
	}

	/**
	 * Optionally specify the endpoint address of this Sender.
	 */
	public void setEndpointAddress(String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}
	
	/**
	 * For debugging purposes: do not actually send a notification.
	 */
	public void setNoNetworking(Boolean offline) {
		this.online = !offline;
	}

}
