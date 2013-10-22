package eu.play_project.play_eventadapter.tests;


import static org.junit.Assert.assertTrue;

import org.event_processing.events.types.CrisisSituationEvent;
import org.event_processing.events.types.Event;
import org.junit.Before;
import org.junit.Test;

import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSenderRest;

public class AbstractSenderRestTest {

	private AbstractSenderRest eventSource;

	@Before
	public void setup() {
		eventSource = new AbstractSenderRest(Stream.FacebookStatusFeed.getTopicUri());
		// Only for unit testing:
		eventSource.setNoNetworking(true);
	}
	
	@Test
	public void testNotify() {
		eventSource.notify("");
		assertTrue(true);
	}
	
	/**
	 * Manual test to send a authenticated notification to a real online
	 * endpoint. (Implying this test has remote sided effects.)
	 */
	public static void main(String[] args) {
		AbstractSenderRest sender = new AbstractSenderRest(Stream.SituationalEventStream.getTopicQName(), "http://play.inria.fr:8080/play/api/v1/platform/publish");
		Event e = EventHelpers.builder()
				.type(CrisisSituationEvent.RDFS_CLASS)
				.stream(Stream.SituationalEventStream)
				.build();
		sender.notify(e);

	}

}
