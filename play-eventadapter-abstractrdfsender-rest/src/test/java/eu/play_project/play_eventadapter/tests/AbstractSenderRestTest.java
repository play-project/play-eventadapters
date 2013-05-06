package eu.play_project.play_eventadapter.tests;

import static eu.play_project.play_commons.constants.Stream.SituationalEventStream;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_eventadapter.AbstractSenderRest;

public class AbstractSenderRestTest {

	private AbstractSenderRest eventSource;

	@Before
	public void setup() {
		eventSource = new AbstractSenderRest(SituationalEventStream.getTopicQName());
		// Only for unit testing:
		eventSource.setNoNetworking(true);
	}
	
	@Test
	public void testConfigurationProperties() {
		Assert.assertNotNull(Constants.getProperties("play-eventadapter.properties").getProperty(
				"play.platform.api.token"));
		Assert.assertNotNull(Constants.getProperties().getProperty(
				"play.platform.endpoint"));
	}
	
	@Test
	public void testNotifyStringQName() {
		eventSource.notify("{'foo' : 'bar'}", null);
		assertTrue(true);
	}

}
