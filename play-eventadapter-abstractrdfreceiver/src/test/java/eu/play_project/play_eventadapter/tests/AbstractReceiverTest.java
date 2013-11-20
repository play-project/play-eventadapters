package eu.play_project.play_eventadapter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.event_processing.events.types.PachubeEvent;
import org.event_processing.events.types.UcTelcoClic2Call;
import org.event_processing.events.types.UcTelcoGeoLocation;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.petalslink.dsb.notification.commons.NotificationException;

import eu.play_project.play_eventadapter.AbstractReceiver;
import eu.play_project.play_eventadapter.NoRdfEventException;

public class AbstractReceiverTest {

	private AbstractReceiver eventConsumer;
	
	@Before
	public void setup() {
		eventConsumer = new AbstractReceiver() {};
	}
	
	@Test
	public void testPachubeNotify() throws IOException {
		try {
			String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("PachubeEvent.notify.xml"), StandardCharsets.UTF_8);
			/*
			 * Read RDF from the XML message
			 */
			Model model = eventConsumer.parseRdf(xmlText);
			assertEquals("Parsed statements in model", 31, model.size());
			
			/*
			 * Instantiate a Pachube event
			 */
			ReactorResult<? extends PachubeEvent> result = PachubeEvent.getAllInstances_as(model);
			List<? extends PachubeEvent> l = result.asList();
			assertEquals("Expecting to find a Pachube event", 1, l.size());
			
			/*
			 * Look into some attributes
			 */
			PachubeEvent event = l.get(0);
			assertEquals("Checking for event timestamp", javax.xml.bind.DatatypeConverter
					.parseDateTime("2013-03-05T23:57:08.653Z"), event.getEndTime());
		} catch (NoRdfEventException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTaxiUCGeoLocationEvent() throws IOException {
		try {
			String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("TaxiUCGeoLocation.notify.xml"), StandardCharsets.UTF_8);
			/*
			 * Read RDF from the XML message
			 */
			Model model = eventConsumer.parseRdf(xmlText);
			assertEquals("Parsed statements in model", 9, model.size());
			
			/*
			 * Instanciate an event object
			 */
			ReactorResult<? extends UcTelcoGeoLocation> result = UcTelcoGeoLocation.getAllInstances_as(model);
			List<? extends UcTelcoGeoLocation> l = result.asList();
			assertEquals("Expecting to find a Pachube event", 1, l.size());
			
			/*
			 * Look into some attributes
			 */
			UcTelcoGeoLocation event = l.get(0);
			assertNotNull("Checking for an event location", event.getLocation());
		} catch (NoRdfEventException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testUcTelcoClic2CallEvent() throws IOException {
		try {
			String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("UcTelcoClic2Call.notify.xml"), StandardCharsets.UTF_8);
			/*
			 * Read RDF from the XML message
			 */
			Model model = eventConsumer.parseRdf(xmlText);
			assertEquals("Parsed statements in model", 7, model.size());
	
			/*
			 * Instantiate an event object
			 */
			ReactorResult<? extends UcTelcoClic2Call> result = UcTelcoClic2Call.getAllInstances_as(model);
			List<? extends UcTelcoClic2Call> l = result.asList();
			assertEquals("Expecting to find a UcTelcoClic2Call event", 1, l.size());
			
			/*
			 * Look into some attributes using the getters:
			 */
			UcTelcoClic2Call clic2callEvent = l.get(0);
			assertNotNull("Checking if there was an event instantiated.", clic2callEvent);
			assertEquals("Checking for an event phone number", "33600000010", clic2callEvent.getUcTelcoCalleePhoneNumber());
			assertEquals("Checking for an event uniqueId", "cl1-24", clic2callEvent.getUcTelcoUniqueId());
		} catch (NoRdfEventException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGenericEvent() throws IOException {
		String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("UcTelcoClic2Call.notify.xml"), StandardCharsets.UTF_8);

		UcTelcoClic2Call clic2callEvent;
		try {
			clic2callEvent = eventConsumer.getEvent(xmlText, UcTelcoClic2Call.class);
		} catch (NoRdfEventException e) {
			fail(e.getMessage());
			return;
		}

		assertNotNull("Checking if there was an event instantiated.", clic2callEvent);
		assertEquals("Checking for an event phone number", "33600000010", clic2callEvent.getUcTelcoCalleePhoneNumber());
		assertEquals("Checking for an event uniqueId", "cl1-24", clic2callEvent.getUcTelcoUniqueId());

	}
	
	/**
	 * This test should throw an exception because an non-rdf event is parsed.
	 * 
	 * @throws NoRdfEventException
	 * @throws IOException
	 */
	@Test(expected = NoRdfEventException.class)
	public void testNonRdfMessageException() throws NoRdfEventException, IOException {
		String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("NonRDFEvent.soap.xml"), StandardCharsets.UTF_8);
		/*
		 * Read RDF from the XML message
		 */
		Model model = eventConsumer.parseRdf(xmlText);
		assertEquals("Parsed statements in model", 9, model.size());

		/*
		 * Instanciate an event object
		 */
		ReactorResult<? extends UcTelcoGeoLocation> result = UcTelcoGeoLocation
				.getAllInstances_as(model);
		List<? extends UcTelcoGeoLocation> l = result.asList();
		assertEquals("Expecting to find a Pachube event", 1, l.size());

		/*
		 * Look into some attributes
		 */
		UcTelcoGeoLocation event = l.get(0);
		assertNotNull("Checking for an event location", event.getLocation());
	}
	
	/**
	 * A manual test, not to be executed automatically because it has side
	 * effects on the remote server.
	 */
	public static void main(String[] args) throws NotificationException {
		AbstractReceiver consumer = new AbstractReceiver() {};

		QName testTopic = new QName("http://example.org/", "NonexistentTopic", "n");
		consumer.subscribe(testTopic, "http://leonberger.fzi.de:8085/Notify");
		
		consumer.unsubscribeAll();
	}

}
