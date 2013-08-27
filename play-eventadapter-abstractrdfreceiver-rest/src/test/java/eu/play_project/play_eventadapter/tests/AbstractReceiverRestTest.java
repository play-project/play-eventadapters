package eu.play_project.play_eventadapter.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter.AbstractReceiverRest;
import eu.play_project.play_eventadapter.NoRdfEventException;

public class AbstractReceiverRestTest {
	
	private static AbstractReceiverRest eventConsumer;
	
	@Before
	public void setup() {
		eventConsumer = new AbstractReceiverRest(){};
	}
	
	@Test
	public void testConfigurationProperties() {
		Assert.assertNotNull(Constants.getProperties("play-eventadapter.properties").getProperty(
				"play.platform.api.token"));
		Assert.assertNotNull(Constants.getProperties().getProperty(
				"play.platform.endpoint"));
	}
	
	/**
	 * Manual test
	 */
	public static void main(String[] args) {
		eventConsumer = new AbstractReceiverRest(){};
		
		/*
		 * test subscribe and unsubscribe
		 */
		String id = eventConsumer.subscribe(Stream.FacebookStatusFeed.getTopicUri(), "http://host:port/foo/bar");
		Assert.assertTrue(!id.equals(""));
		eventConsumer.unsubscribe(id);
		Assert.assertTrue(true);


		/*
		 * test GetTopics
		 */
		String id1 = eventConsumer.subscribe(Stream.FacebookStatusFeed.getTopicUri(), "http://host:port/foo/bar");
		Assert.assertTrue(!id1.equals(""));
		
		String id2 = eventConsumer.subscribe(Stream.TwitterFeed.getTopicUri(), "http://host:port/foo/bar");
		Assert.assertTrue(!id2.equals(""));
		
		List<String> topics = eventConsumer.getTopics();
		Assert.assertTrue(topics.size()>=2);
		Assert.assertTrue(topics.get(0).equalsIgnoreCase(Stream.FacebookStatusFeed.getTopicUri()+"#stream")
						|| topics.get(0).equalsIgnoreCase(Stream.TaxiUCCall.getTopicUri()+"#stream"));
		Assert.assertTrue(topics.get(1).equalsIgnoreCase(Stream.FacebookStatusFeed.getTopicUri()+"#stream")
				|| topics.get(1).equalsIgnoreCase(Stream.TaxiUCCall.getTopicUri()+"#stream"));
		
		eventConsumer.unsubscribe(id1);
		eventConsumer.unsubscribe(id2);
		Assert.assertTrue(true);
	}
	
	@Test(expected = NoRdfEventException.class)
	public void testParseRdf() throws NoRdfEventException {
		eventConsumer.parseRdfRest("");
	}
}
