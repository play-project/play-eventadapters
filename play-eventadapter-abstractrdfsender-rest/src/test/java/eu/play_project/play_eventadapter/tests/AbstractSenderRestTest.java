package eu.play_project.play_eventadapter.tests;

import org.junit.Assert;
import org.junit.Test;

import eu.play_project.play_commons.constants.Constants;

public class AbstractSenderRestTest {

	/**
	 * Confirm that important configuration properties for event sending are
	 * set.
	 */
	@Test
	public void testConfigurationProperties() {
		Assert.assertNotNull(Constants.getProperties("play-eventadapter.properties").getProperty(
				"play.platform.api.token"));
		Assert.assertNotNull(Constants.getProperties().getProperty(
				"play.platform.endpoint"));
	}
}
