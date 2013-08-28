package eu.play_project.play_eventadapter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.event_processing.events.types.UcTelcoClic2Call;
import org.event_processing.events.types.UcTelcoGeoLocation;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdfreactor.runtime.ReactorResult;

import eu.play_project.play_eventadapter.AbstractReceiverRest;
import eu.play_project.play_eventadapter.NoRdfEventException;

/**
 * Test for old SOAP functionality. Although outgoing operations (sending
 * events, sending subscriptions) are RESTful we still must accept some SOAP
 * calls e.g. when receiveing events.
 */
public class AbstractReceiverSoapTest {

	private AbstractReceiverRest eventConsumer;
	
	@Before
	public void setup() {
		eventConsumer = new AbstractReceiverRest() {};
	}
	
	@Test
	public void testGenericEvent() throws IOException {
		String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("UcTelcoClic2Call.notify.xml"), "UTF-8");

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
		String xmlText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("NonRDFEvent.soap.xml"), "UTF-8");
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
	
}
