package eu.play_project.play_eventadapter.tests;


import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import eu.play_project.play_commons.constants.Stream;
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
	public void testNotifyStringQName() {
		eventSource.notify("{'foo' : 'bar'}");
		assertTrue(true);
	}

}
