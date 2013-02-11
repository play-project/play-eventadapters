package eu.play_project.play_eventadapter_twitter.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import eu.play_project.play_eventadapter_twitter.TwitterProperties;

public class TwitterPropertiesTest {

	@Test
	public void testTwitterProperties() throws IOException {
		new TwitterProperties();
		assertNotNull(TwitterProperties.getTwitter1ConsumerKey());
		// This test is useless for CI because CI does not have the passwords:
		//assertTrue("Twitter credentials must be added to the properties file, e.g. by Maven.", !TwitterProperties.getTwitter1ConsumerKey().contains("${"));
	}
}
