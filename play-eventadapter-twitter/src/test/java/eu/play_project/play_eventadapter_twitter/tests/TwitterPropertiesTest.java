package eu.play_project.play_eventadapter_twitter.tests;

import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

import eu.play_project.play_eventadapter_twitter.TwitterProperties;

public class TwitterPropertiesTest {

	@Test
	public void testTwitterProperties() throws IOException {
		new TwitterProperties();
		assertNotNull(TwitterProperties.getTwitter1ConsumerKey());
		assertTrue("Twitter credentials must be added to the properties file, e.g. by Maven.", !TwitterProperties.getTwitter1ConsumerKey().contains("${"));
	}
}
