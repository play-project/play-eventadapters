package eu.play_project.play_eventadapter.tests;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;
import static eu.play_project.play_commons.constants.Stream.SituationalEventStream;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.event_processing.events.types.CrisisMeasureEvent;
import org.event_processing.events.types.Event;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.play_project.platformservices.eventvalidation.InvalidEventException;
import eu.play_project.platformservices.eventvalidation.Validator;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSender;

public class AbstractSenderTest {

	private AbstractSender eventSource;

	@Before
	public void setup() {
		eventSource = new AbstractSender(SituationalEventStream.getTopicQName());
		// Only for unit testing:
		eventSource.setNoNetworking(true);
	}

	/**
	 * To create and populate the event object this test uses the builder from
	 * {@link EventHelpers#builder()}.
	 */
	@Test
	public void testNotifyBuilder() {
		String eventId = EventHelpers.createRandomEventId("crisis");

		Event event = EventHelpers.builder(eventId)
				.type(CrisisMeasureEvent.RDFS_CLASS)
				.stream(SituationalEventStream)
				.addProperty(CrisisMeasureEvent.CRISISFREQUENCY, "1000")
				.addProperty(CrisisMeasureEvent.CRISISCOMPONENTNAME, "Component-101")
				.addProperty(CrisisMeasureEvent.CRISISLOCALISATION, "somewhere")
				.addProperty(CrisisMeasureEvent.CRISISSITUATION, "Sit-01")
				.addProperty(CrisisMeasureEvent.CRISISUID, eventId)
				.addProperty(CrisisMeasureEvent.CRISISUNIT, "MHz")
				.addProperty(CrisisMeasureEvent.CRISISVALUE, "123")
				.addProperty(CrisisMeasureEvent.CRISISCOMPONENTSEID, "someSEID")
				.build();
		
		eventSource.notify(event, SituationalEventStream.getTopicQName());
		
		// Only for testing: send everything to a validator:
		Validator v = new Validator().checkModel(event.getModel().getContextURI(), event.getModel());
		try {
			assertTrue("Make sure that the created event meets some PLAY standards.", v.isValid());
		} catch (InvalidEventException e) {
			fail("Make sure that the created event meets some PLAY standards.");
		}
	}
	
	
	/**
	 * To create and populate the event object this test uses the RDF SDK by
	 * instantiating a (generated) class {@link CrisisMeasureEvent}.
	 */
	@Test
	public void testNotifyModel() {
		// Create an event ID used in RDF context and RDF subject
		String eventId = EventHelpers.createRandomEventId("crisis");

		CrisisMeasureEvent event = new CrisisMeasureEvent(
				// set the RDF context part
				EventHelpers.createEmptyModel(eventId),
				// set the RDF subject
				eventId + EVENT_ID_SUFFIX,
				// automatically write the rdf:type statement
				true);

		event.setStream(new URIImpl(SituationalEventStream.getUri()));
		event.setEndTime(Calendar.getInstance());
		
		event.setCrisisFrequency("1000");
		event.setCrisisComponentName("Component-101");
		event.setCrisisLocalisation("somewhere");
		event.setCrisisSituation("Sit-01");
		event.setCrisisUid(eventId);
		event.setCrisisUnit("MHz");
		event.setCrisisValue("123");
		event.setCrisisComponentSeid("someSEID");
		
		eventSource.notify(event, SituationalEventStream.getTopicQName());
		
		// Only for testing: send everything to a validator:
		Validator v = new Validator().checkModel(event.getModel().getContextURI(), event.getModel());
		try {
			assertTrue("Make sure that the created event meets some PLAY standards.", v.isValid());
		} catch (InvalidEventException e) {
			fail("Make sure that the created event meets some PLAY standards.");
		}
	}
}
