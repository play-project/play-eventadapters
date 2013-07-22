package eu.play_project.play_eventadapter_twitter.tests;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_eventadapter_twitter.TwitterProperties;

public class TwitterPropertiesTest {

	@Test
	public void testTwitterProperties() throws IOException {
		new TwitterProperties();
		assertNotNull(TwitterProperties.getTwitter1ConsumerKey());
		// This test is useless for CI because CI does not have the passwords:
		//assertTrue("Twitter credentials must be added to the properties file, e.g. by Maven.", !TwitterProperties.getTwitter1ConsumerKey().contains("${"));
	}
	
	@Test
	public void testConfigurationProperties() {
		Assert.assertNotNull(Constants.getProperties("play-eventadapter.properties").getProperty(
				"play.platform.api.token"));
		Assert.assertNotNull(Constants.getProperties().getProperty(
				"play.platform.endpoint"));
	}
}
